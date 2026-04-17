package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TextRepairReport {

    private boolean dryRun;

    private int scannedUserCount;

    private int scannedInterfaceCount;

    private int updatedUserNameCount;

    private int updatedInterfaceNameCount;

    private int updatedInterfaceDescriptionCount;

    private int placeholderUserNameCount;

    private int placeholderInterfaceNameCount;

    private int placeholderInterfaceDescriptionCount;

    private int totalUpdatedCount;

    private List<String> samples = new ArrayList<>();
}

