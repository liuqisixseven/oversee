package com.chd.modules.oversee.issue.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OverseeIssueFiles extends OverseeIssueFile {
    private String url;//附件Url
    private String fileName;//附件名称
    private String relativePath;//附件路径
}
