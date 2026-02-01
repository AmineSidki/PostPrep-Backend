package org.aminesidki.postprep.entity;

import java.util.List;

public record OutputJson(String summary ,
                         List<String> categories ,
                         String seoTitle ,
                         Double confidenceScore ,
                         List<String> keywords) {}
