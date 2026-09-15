package com.tts.reports.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Address {

    private Long id;

    private String street;

    private String pinCode;

    private String state;

    private String country;

    @Builder.Default()
    private boolean deleted = false;

}