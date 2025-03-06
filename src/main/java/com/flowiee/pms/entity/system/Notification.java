package com.flowiee.pms.entity.system;

import com.flowiee.pms.base.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity implements Serializable {
	static final long serialVersionUID = 1L;

	@Column(name = "send", nullable = false)
    Long send;

    @Column(name = "receive", nullable = false)
    Long receive;

    @Column(name = "type", nullable = false)
    String type;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content", nullable = false, length = 1000)
    String content;

    @Column(name = "readed", nullable = false)
    Boolean readed;

    @Column(name = "import_id")
    Long importId;

	@Override
	public String toString() {
		return "Notification [id=" + super.id + ", send=" + send + ", receive=" + receive + ", type=" + type + ", title=" + title
				+ ", content=" + content + ", readed=" + readed + ", importId=" + importId + "]";
	}        
}