package edu.platform;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithTest {

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(EduPlatformApplication.class);
        modules.verify();
    }
}