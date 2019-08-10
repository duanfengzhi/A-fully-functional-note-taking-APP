package com.hfut.cqyzs.memorandum.bean.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContentAddRecordTime {
    @JsonProperty("contentId")
    int content_id;
    @JsonProperty("recordId")
    int record_id;
    @JsonProperty("recordContent")
    String record_content;
    @JsonProperty("contentType")
    int content_type;
    @JsonProperty("contentOrder")
    int content_order;
    @JsonProperty("recordCreateTime")
    String recordCreateTime;
}
