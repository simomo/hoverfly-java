package io.specto.hoverfly.junit.rule;

import org.junit.Test;

import java.nio.file.Paths;

import static io.specto.hoverfly.junit.rule.HoverflyRuleUtils.fileRelativeToTestResourcesHoverfly;
import static org.assertj.core.api.Assertions.assertThat;

public class HoverflyRuleUtilsTest {
    @Test
    public void relativeFilesShouldAlwaysHaveAForwardSlashAfterHoverfly() {
        assertThat(fileRelativeToTestResourcesHoverfly("after-hoverfly.json"))
                .isEqualTo(Paths.get("src", "test", "resources", "hoverfly", "after-hoverfly.json"));
    }
}