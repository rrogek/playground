package com.throttling.limiters.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Message {
    String id;
    String customerId;
    String type;
    String content;


}
