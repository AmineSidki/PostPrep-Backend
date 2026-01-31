package org.aminesidki.postprep.entity;

import java.util.List;

public record OutputJson(String summary ,
                         String category ,
                         String seoTitle ,
                         Integer confidenceScore ,
                         List<String> keywords) {}
