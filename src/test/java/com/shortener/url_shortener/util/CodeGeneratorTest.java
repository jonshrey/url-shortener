package com.shortener.url_shortener.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CodeGeneratorTest {

    @Test
    void shouldGenerateCodeOfSpecifiedLength() {
        String code = CodeGenerator.generate(8);
        assertThat(code).hasSize(8);
    }

    @Test
    void shouldGenerateOnlyAlphaNumericCharacters() {
        String code = CodeGenerator.generate(8);
        assertThat(code).matches("^[a-zA-Z0-9]+$");
    }

    @Test
    void shouldGenerateDifferentCodesOnSubsequentCalls() {
        String code1 = CodeGenerator.generate(8);
        String code2 = CodeGenerator.generate(8);
        assertThat(code1).isNotEqualTo(code2);
    }
}