package com.flowiee.pms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.base.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Builder
@Entity
@Table(name = "import_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportHistory extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @Column(name = "title", nullable = false, length = 999)
    String title;

	@Column(name = "module")
    String module;

    @Column(name = "entity")
    String entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", nullable = false)
    Account account;

    @Column(name = "begin_time", nullable = false)
    LocalTime beginTime;

    @Column(name = "finish_time", nullable = false)
    LocalTime finishTime;

    @Column(name = "total_record")
    Integer totalRecord;

    @Column(name = "result", nullable = false)
    String result;

    @Column(name = "file_path", nullable = false)
    String filePath;

    @JsonIgnore
    @OneToMany(mappedBy = "importHistory", fetch = FetchType.LAZY)
    List<ImportHistoryDetail> importHistoryDetailList;

	@Override
	public String toString() {
		return "FileImportHistory [id=" + super.id + ", module=" + module + ", entity=" + entity + ", account=" + account.getUsername()
                + ", beginTime=" + beginTime + ", finishTime=" + finishTime + ", totalRecord=" + totalRecord + ", result=" + result + "]";
	}
}