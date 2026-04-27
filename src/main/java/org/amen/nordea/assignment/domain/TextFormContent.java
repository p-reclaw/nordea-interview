package org.amen.nordea.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextFormContent {
    private String inputText;
    private String expectedResult;
    private String actualResult;

}
