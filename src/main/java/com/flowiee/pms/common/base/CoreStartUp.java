package com.flowiee.pms.common.base;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.config.TemplateSendEmail;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.schedule.entity.Schedule;
import com.flowiee.pms.modules.system.entity.Branch;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.system.repository.BranchRepository;
import com.flowiee.pms.modules.system.service.ConfigService;
import com.flowiee.pms.modules.system.service.LanguageService;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.user.entity.GroupAccount;
import com.flowiee.pms.modules.system.model.ServerInfo;
import com.flowiee.pms.modules.category.repository.CategoryRepository;
import com.flowiee.pms.modules.sales.repository.CustomerRepository;
import com.flowiee.pms.common.utils.CommonUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.utils.PasswordUtils;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.EndPoint;
import com.flowiee.pms.common.enumeration.NotificationType;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.modules.system.repository.ScheduleRepository;
import com.flowiee.pms.modules.user.repository.AccountRepository;
import com.flowiee.pms.modules.user.repository.GroupAccountRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class CoreStartUp {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ConfigRepository mvConfigRepository;
	private final BranchRepository mvBranchRepository;
	private final AccountRepository mvAccountRepository;
	private final LanguageService mvLanguageService;
	private final CustomerRepository     mvCustomerRepository;
	private final CategoryRepository     mvCategoryRepository;
	private final GroupAccountRepository mvGroupAccountRepository;
	private final ConfigService mvConfigService;
	private final TemplateSendEmail      mvTemplateSendEmail;
	private final Environment            mvEnvironment;
	private final ScheduleRepository mvScheduleRepository;
	private final SessionRegistry 		 mvSessionRegistry;

	public static LocalDateTime                                      START_APP_TIME;
	public static String                                             mvResourceUploadPath      = null;
	private static Map<NotificationType, TemplateSendEmail.Template> mvGeneralEmailTemplateMap = new HashMap<>();
	private static Map<ConfigCode, SystemConfig>                     mvSystemConfigList        = new HashMap();
	private static final ConfigCode                                  mvConfigInitData          = ConfigCode.initData;

	private static final int CATEGORY_TYPE_COL_INDEX = 0;
	private static final int CATEGORY_CODE_COL_INDEX = 1;
	private static final int CATEGORY_NAME_COL_INDEX = 2;
	private static final int CATEGORY_STATUS_COL_INDEX = 3;
	private static final int CATEGORY_ISDEFAULT_COL_INDEX = 4;
	private static final int CATEGORY_ENDPOINT_COL_INDEX = 5;
	private static final int BRANCH_CODE_COL_INDEX = 0;
	private static final int BRANCH_NAME_COL_INDEX = 1;
	private static final int GROUP_ACCOUNT_CODE_COL_INDEX = 0;
	private static final int GROUP_ACCOUNT_NAME_COL_INDEX = 1;
	private static final int ACCOUNT_USERNAME_COL_INDEX = 0;
	private static final int ACCOUNT_PASSWORD_COL_INDEX = 1;
	private static final int ACCOUNT_FULLNAME_COL_INDEX = 2;
	private static final int ACCOUNT_SEX_COL_INDEX = 3;
	private static final int ACCOUNT_ROLE_COL_INDEX = 4;
	private static final int ACCOUNT_BRANCH_CODE_COL_INDEX = 5;
	private static final int ACCOUNT_GROUPACCOUNT_CODE_COL_INDEX = 6;
	private static final int ACCOUNT_STATUS_COL_INDEX = 7;
	private static final int CUSTOMER_CODE_COL_INDEX = 0;
	private static final int CUSTOMER_NAME_COL_INDEX = 1;
	private static final int CUSTOMER_SEX_COL_INDEX = 2;
	private static final int SCHEDULE_ID_COL_INDEX = 0;
	private static final int SCHEDULE_NAME_COL_INDEX = 1;
	private static final int SCHEDULE_ENABLE_COL_INDEX = 2;

    @Bean
    CommandLineRunner init() {
    	return args -> {
			String[] lvActiveProfiles = mvEnvironment.getActiveProfiles();
			logger.info("Running in {} environment", lvActiveProfiles[0].toUpperCase());

    		initConfig();
			initData();
			configReport();
            configEndPoint();

            mvConfigService.refreshApp();
			logger.info("System configuration loading finished");

			List<TemplateSendEmail.Template> lvGeneralMailTemplates = mvTemplateSendEmail.getGeneralMailTemplates();
			lvGeneralMailTemplates.forEach(lvTemplate -> {
				NotificationType lvNotificationType = NotificationType.valueOf(lvTemplate.getType());
				String lvEncoding = lvTemplate.getEncoding();
				String lvTemplatePath = lvTemplate.getPath();
				StringBuilder lvTemplateContent = new StringBuilder("");
				if (Files.exists(Path.of(lvTemplatePath))) {
					byte[] lvBuf = new byte[1024];
					try (BufferedInputStream lvIs = new BufferedInputStream(new FileInputStream(new File(lvTemplatePath)));
						 ByteArrayOutputStream lvOs = new ByteArrayOutputStream()) {
						int lvBytesRead = -1;
						while ((lvBytesRead = lvIs.read(lvBuf)) != -1) {
							lvOs.write(lvBuf, 0, lvBytesRead);
						}
						lvTemplateContent.append(new String(lvOs.toByteArray(), lvEncoding));
					} catch (IOException e) {
						logger.warn(e.getMessage(), e);
					}
				}
				lvTemplate.setTemplateContent(lvTemplateContent.toString());
				mvGeneralEmailTemplateMap.put(lvNotificationType, lvTemplate);
			});
			logger.info("Email template loading finished");

			expireAllSessions();

			START_APP_TIME = LocalDateTime.now();
        };
    }

	@EventListener
	private void loadServerInfo(WebServerInitializedEvent event) {
		int serverPort = event.getWebServer().getPort();
		String ipAddress = "localhost";
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			Log.info("Can't get local host address");
		}
		CommonUtils.mvServerInfo = new ServerInfo(ipAddress, serverPort);
		logger.info("Server is running on IP: " + ipAddress + ", Port: " + serverPort);
	}

	private void configReport() {
		String templateExportTempStr = FileUtils.excelTemplatePath + "/temp";
		Path templateExportTempPath = Paths.get(templateExportTempStr);
		if (!Files.exists(templateExportTempPath)) {
            try {
                Files.createDirectories(templateExportTempPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
	}

	private void configEndPoint() {
		CommonUtils.mvEndPointHeaderConfig.clear();
		CommonUtils.mvEndPointSideBarConfig.clear();
		for (EndPoint e : EndPoint.values()) {

			if (e.getType().equals("HEADER") && e.isStatus()) {
				CommonUtils.mvEndPointHeaderConfig.put(e.name(), e.getValue());
			}
			if (e.getType().equals("SIDEBAR") && e.isStatus()) {
				CommonUtils.mvEndPointSideBarConfig.put(e.name(), e.getValue());
			}
		}
	}

	private void initConfig() {
		List<SystemConfig> cnfList = initConfigModels(ConfigCode.values());

		List<SystemConfig> lvSystemConfigList = mvConfigRepository.findAll();
		Map<ConfigCode, SystemConfig> lvSystemConfigMap = lvSystemConfigList.stream()
				.collect(Collectors.toMap(
						cnf -> ConfigCode.get(CoreUtils.trim(cnf.getCode())),
						cnf -> cnf,
						(existing, replacement) -> existing
				));

		SystemConfig flagConfigObj = lvSystemConfigMap.get(mvConfigInitData);
		if (flagConfigObj == null) {
			SystemConfig cnfModel = new SystemConfig(mvConfigInitData.name(), mvConfigInitData.getDescription(), "N");
			initAudit(cnfModel);
			cnfList.add(cnfModel);
		}

		initNewConfigIfDatabaseNotDefined(cnfList);

		for (SystemConfig systemConfig : mvConfigRepository.findAll()) {
			ConfigCode lvConfigCode = ConfigCode.get(systemConfig.getCode());
			if (lvConfigCode == null)
				continue;
			mvSystemConfigList.put(lvConfigCode, systemConfig);
		}

		mvResourceUploadPath = mvSystemConfigList.get(ConfigCode.resourceUploadPath).getValue();
		CommonUtils.defaultNewPassword = mvSystemConfigList.get(ConfigCode.generateNewPasswordDefault).getValue();
	}

	@Transactional
	void initData() throws Exception {
		SystemConfig systemConfigInitData = mvSystemConfigList.get(mvConfigInitData);
		if ("Y".equals(systemConfigInitData.getValue())) {
			return;
		}
		//Init category
		File lvCsvDataFile = FileUtils.getFileDataCategoryInit();
		CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
		FileReader lvFileReader = new FileReader(lvCsvDataFile);
		CSVReader lvCsvReader = new CSVReaderBuilder(lvFileReader).withCSVParser(parser).build();
		List<Category> lvListCategory = new ArrayList<>();
		int lineCsv = 0;
		for (String[] row : lvCsvReader.readAll()) {
			if (lineCsv > 0) {//header
				Category category = Category.builder()
						.type(CoreUtils.trim(row[CATEGORY_TYPE_COL_INDEX]))
						.code(CoreUtils.trim(row[CATEGORY_CODE_COL_INDEX]))
						.name(CoreUtils.trim(row[CATEGORY_NAME_COL_INDEX]))
						.status(true)//.status(Boolean.parseBoolean(CoreUtils.trim(row[CATEGORY_STATUS_COL_INDEX])))
						.isDefault(CoreUtils.trim(row[CATEGORY_ISDEFAULT_COL_INDEX]))
						.endpoint(CoreUtils.trim(row[CATEGORY_ENDPOINT_COL_INDEX]))
						.build();
				initAudit(category);
				lvListCategory.add(category);
			}
			lineCsv ++;
		}
		lvListCategory.remove(0);//header
		mvCategoryRepository.saveAll(lvListCategory);
		lvFileReader.close();
		lvCsvReader.close();

		String[] lvSheets = new String[]{"BRANCH", "GROUP_ACCOUNT", "ACCOUNT", "CUSTOMER", "SCHEDULE"};
		File lvXlsxDataFile = FileUtils.getFileDataSystemInit();
		XSSFWorkbook lvWorkbook = new XSSFWorkbook(lvXlsxDataFile);
		for (String lvSheetName : lvSheets) {
			XSSFSheet lvSheet = lvWorkbook.getSheet(lvSheetName);
			if (lvSheet == null) {
				continue;
			}
			switch (lvSheetName) {
				case "BRANCH":
					for (int i = 0; i < lvSheet.getPhysicalNumberOfRows(); i++) {
						XSSFRow lvRow = lvSheet.getRow(i + 1);
						if (lvRow == null) continue;
						Branch lvBranch = Branch.builder()
								.branchCode(getValue(lvRow, BRANCH_CODE_COL_INDEX))
								.branchName(getValue(lvRow, BRANCH_NAME_COL_INDEX))
								.build();
						initAudit(lvBranch);
						try {
							mvBranchRepository.save(lvBranch);
						} catch (DataIntegrityViolationException ex) {}
					}
					break;
				case "GROUP_ACCOUNT":
					for (int i = 0; i < lvSheet.getPhysicalNumberOfRows(); i++) {
						XSSFRow lvRow = lvSheet.getRow(i + 1);
						if (lvRow == null) continue;
						GroupAccount lvGroupAccount = GroupAccount.builder()
								.groupCode(getValue(lvRow, GROUP_ACCOUNT_CODE_COL_INDEX))
								.groupName(getValue(lvRow, GROUP_ACCOUNT_NAME_COL_INDEX))
								.build();
						initAudit(lvGroupAccount);
						try {
							mvGroupAccountRepository.save(lvGroupAccount);
						} catch (DataIntegrityViolationException ex) {}
					}
					break;
				case "ACCOUNT":
					for (int i = 0; i < lvSheet.getPhysicalNumberOfRows(); i++) {
						XSSFRow lvRow = lvSheet.getRow(i + 1);
						if (lvRow == null) continue;
						String lvBranchCode = getValue(lvRow, ACCOUNT_BRANCH_CODE_COL_INDEX);
						String lvGroupCode = getValue(lvRow, ACCOUNT_GROUPACCOUNT_CODE_COL_INDEX);
						Account lvAccount = Account.builder()
								.username(getValue(lvRow, ACCOUNT_USERNAME_COL_INDEX))
								.password(PasswordUtils.encodePassword(getValue(lvRow, ACCOUNT_PASSWORD_COL_INDEX)))
								.fullName(getValue(lvRow, ACCOUNT_FULLNAME_COL_INDEX))
								.sex(getValue(lvRow, ACCOUNT_SEX_COL_INDEX).equals("M"))
								.role(getValue(lvRow, ACCOUNT_ROLE_COL_INDEX))
								.branch(mvBranchRepository.findByCode(lvBranchCode))
								.groupAccount(mvGroupAccountRepository.findByCode(lvGroupCode))
								.status(getValue(lvRow, ACCOUNT_STATUS_COL_INDEX))
								.build();
						initAudit(lvAccount);
						try {
							mvAccountRepository.save(lvAccount);
						} catch (DataIntegrityViolationException ex) {}
					}
					break;
				case "CUSTOMER":
					for (int i = 0; i < lvSheet.getPhysicalNumberOfRows(); i++) {
						XSSFRow lvRow = lvSheet.getRow(i + 1);
						if (lvRow == null) continue;
						Customer lvCustomer = Customer.builder()
								.code(getValue(lvRow, CUSTOMER_CODE_COL_INDEX))
								.customerName(getValue(lvRow, CUSTOMER_NAME_COL_INDEX))
								.dateOfBirth(LocalDate.now())
								.gender(getValue(lvRow, CUSTOMER_SEX_COL_INDEX))
								.build();
						initAudit(lvCustomer);
						try {
							mvCustomerRepository.save(lvCustomer);
						} catch (DataIntegrityViolationException ex) {}
					}
					break;
				case "SCHEDULE":
					for (int i = 0; i < lvSheet.getPhysicalNumberOfRows(); i++) {
						XSSFRow lvRow = lvSheet.getRow(i + 1);
						if (lvRow == null) continue;
						try {
							mvScheduleRepository.save(Schedule.builder()
									.scheduleId(getValue(lvRow, SCHEDULE_ID_COL_INDEX))
									.scheduleName(getValue(lvRow, SCHEDULE_NAME_COL_INDEX))
									.enable(getValue(lvRow, SCHEDULE_ENABLE_COL_INDEX).equals("Y"))
									.build());
						} catch (DataIntegrityViolationException ex) {}
					}
					break;
			}
		}
		lvWorkbook.close();

		systemConfigInitData.setValue("Y");
		mvConfigRepository.save(systemConfigInitData);
	}

	private List<SystemConfig> initConfigModels(ConfigCode[] configs) {
		List<SystemConfig> systemConfigList = new ArrayList<>();
    	for (ConfigCode c : configs) {
			SystemConfig cnfModel = new SystemConfig(c.name(), c.getDescription(), c.getDefaultValue());
			initAudit(cnfModel);
			systemConfigList.add(cnfModel);
		}
		return systemConfigList;
	}

	private BaseEntity initAudit(BaseEntity baseEntity) {
		baseEntity.setCreatedBy(-1l);
		baseEntity.setLastUpdatedBy("SA");
		return baseEntity;
	}

	public static String getResourceUploadPath() {
		return mvResourceUploadPath;
	}

	private void initNewConfigIfDatabaseNotDefined(List<SystemConfig> pInitCnfList) {
		//Current configs
		List<SystemConfig> lvSystemConfigList = mvConfigRepository.findByCode(pInitCnfList.stream().
				map(SystemConfig::getCode)
				.collect(Collectors.toList()));
		List<String> lvConfigCodeList = lvSystemConfigList.stream()
				.map(SystemConfig::getCode)
				.collect(Collectors.toList());
		//Auto add new configs if not define yet
		for (SystemConfig lvSystemConfig  : pInitCnfList) {
			if (!lvConfigCodeList.contains(lvSystemConfig.getCode())) {
				mvConfigRepository.save(lvSystemConfig);
			}
		}
	}

	private String getValue(XSSFRow pRow, int pIndex) {
    	return CoreUtils.trim(pRow.getCell(pIndex).toString());
	}

	public static Map<NotificationType, TemplateSendEmail.Template> getEmailTemplateConfigs() {
		return mvGeneralEmailTemplateMap;
	}

	public static Map<ConfigCode, SystemConfig> getSystemConfigs() {
    	return mvSystemConfigList;
	}

	public void expireAllSessions() {
		for (Object principal : mvSessionRegistry.getAllPrincipals()) {
			List<SessionInformation> sessions = mvSessionRegistry.getAllSessions(principal, false);
			for (SessionInformation session : sessions) {
				session.expireNow();
			}
		}
	}

//	private final List<BaseScheduleStartUp> schedules;
//	@EventListener(ApplicationReadyEvent.class)
//	public void onApplicationReady() {
//		schedules.forEach(schedule -> {
//			try {
//				schedule.run();
//			} catch (Exception e) {
//				System.err.println("Error running " + schedule.getClass().getSimpleName() + ": " + e.getMessage());
//			}
//		});
//	}
}