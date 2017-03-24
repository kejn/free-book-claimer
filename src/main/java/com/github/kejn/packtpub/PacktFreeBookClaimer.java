package com.github.kejn.packtpub;

import com.github.kejn.packtpub.pages.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Root of execution.
 *
 * @author kejn
 */
class PacktFreeBookClaimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacktFreeBookClaimer.class);

    private static final int EXIT_STATUS_DRIVER_NOT_PROVIDED = 1;
    private static final int EXIT_STATUS_DRIVER_INITIALIZATION_ERROR = 2;
    private static final int EXIT_STATUS_ERROR_READING_INTERNAL_SCRIPT = 3;
    private static final int EXIT_STATUS_UNKNOWN_ERROR = -1;

    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.info("Usage:\n\tjava -Dwebdriver.xxx.driver=path\\to\\webdriver -jar "
                    + PacktFreeBookClaimer.class.getSimpleName() + " username password");
            return;
        }
        String username = args[0];
        String password = args[1];

        try {
            WebDriver driver = SupportedDriver.getAvailableDriver();
            PageFactory.initElements(driver, Page.class)
                    .login(username, password)
                    .navigateToFreeBook()
                    .claimYourFreeBook()
                    .logout();
            driver.quit();
        } catch (Exception ex) {
            handleExceptionAndExit(ex);
        }
    }

    private static void handleExceptionAndExit(Exception ex) {
        String errorMessage = "Unknown error";
        int exitStatus = EXIT_STATUS_UNKNOWN_ERROR;

        if (ex instanceof IllegalStateException) {
            String supportedDrivers = Arrays.stream(SupportedDriver.values())
                    .map(SupportedDriver::toString)
                    .collect(Collectors.joining("\n"));
            errorMessage = "Supported drivers:\n" + supportedDrivers;
            exitStatus = EXIT_STATUS_DRIVER_NOT_PROVIDED;
        } else if (ex instanceof ReflectiveOperationException) {
            errorMessage = "Error initializing web driver";
            exitStatus = EXIT_STATUS_DRIVER_INITIALIZATION_ERROR;
        } else if (ex instanceof IOException) {
            errorMessage = "Error reading internal script";
            exitStatus = EXIT_STATUS_ERROR_READING_INTERNAL_SCRIPT;
        }

        LOGGER.error(errorMessage, ex);
        System.exit(exitStatus);
    }

}
