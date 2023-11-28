package com.flowiee.app.controller;

import com.flowiee.app.base.BaseController;
import com.flowiee.app.utils.PagesUtil;
import com.flowiee.app.security.author.ValidateModuleSystem;
import com.flowiee.app.service.NotificationService;
import com.flowiee.app.service.SystemLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/he-thong/nhat-ky")
public class LogController extends BaseController {
    @Autowired
    private SystemLogService systemLogService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    ValidateModuleSystem validateModuleSystem;

    @GetMapping(value = "")
    public ModelAndView getAllLog() {
        if (!accountService.isLogin()) {
            return new ModelAndView(PagesUtil.SYS_LOGIN);
        }
        if (!validateModuleSystem.readLog()) {
            return new ModelAndView(PagesUtil.SYS_UNAUTHORIZED);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtil.SYS_LOG);
        modelAndView.addObject("listLog", systemLogService.getAll());        
        return baseView(modelAndView);
    }

//    @GetMapping(value = "export")
//    public ResponseEntity<?> exportLog(){
//        String username = accountService.getUserName();
//        String nameSheet = "SystemLog" + "_" + DateUtil.now("yyyyMMdd HHmmss");
//        String fileName = "SystemLog" + "_" + DateUtil.now("yyyyMMdd HHmmss");
//        if (username != null && !username.isEmpty()){
//            try {
//                XSSFWorkbook workbook = new XSSFWorkbook();
//                XSSFSheet sheet = workbook.createSheet(nameSheet);
//                // Tạo header
//                XSSFRow rowHead = sheet.createRow(0);
//                rowHead.createCell(0).setCellValue("ID");
//                rowHead.createCell(1).setCellValue("Module");
//                rowHead.createCell(2).setCellValue("Hành động");
//                rowHead.createCell(3).setCellValue("Tài khoản");
//                rowHead.createCell(4).setCellValue("Thời gian");
//                rowHead.createCell(5).setCellValue("Địa chỉ IP");
//
//                // Fill nội dung
//                List<SystemLog> list = systemLogService.getAll();
//                for (int i = 1; i < list.size(); i++) {
//                    XSSFRow row = sheet.createRow(i);
//                    row.createCell(0).setCellValue(list.get(i).getId());
//                    row.createCell(1).setCellValue(list.get(i).getModule());
//                    row.createCell(2).setCellValue(list.get(i).getAction());
//                    row.createCell(3).setCellValue(list.get(i).getCreatedBy());
//                    row.createCell(4).setCellValue(list.get(i).getCreatedBy());
//                    row.createCell(5).setCellValue(list.get(i).getIp());
//                }
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                HttpHeaders header = new HttpHeaders();
//                header.setContentType(new MediaType("application", "force-download"));
//                header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
//                workbook.write(stream);
//                workbook.close();
//
//                systemLogService.writeLog(new SystemLog("Hệ thống", username, "Xuất excel log hệ thống", accountService.getIP()));
//                return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()), header, HttpStatus.CREATED);
//            } catch (Exception e){
//                e.printStackTrace();
//                System.out.println(e.getCause());
//                return null;
//            }
//        }
//        return null;
//    }
}