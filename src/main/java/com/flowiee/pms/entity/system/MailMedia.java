package com.flowiee.pms.entity.system;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "mail_media")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MailMedia implements Serializable {
    public static String EMAIL_ADDRESS_SEPARATOR = ";";
    public static String SUBJECT = "emailSubject";
    public static String DESTINATION = "emailDestination";
    public static String ROWs = "ROWs";
    public static int P_HIGH = 0;
    public static int P_MEDIUM = 1;
    public static int P_LOW = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "destination", nullable = false, length = 500)
    private String destination;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "language", nullable = false, length = 2)
    private String language;

    @Lob
    @Column(name = "message", nullable = false, columnDefinition = "CLOB")
    private String message;

    @Column(name = "is_html", nullable = false)
    private boolean isHtml;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "attachment")
    private String attachment;
}