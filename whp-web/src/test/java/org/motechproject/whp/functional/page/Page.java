package org.motechproject.whp.functional.page;

import org.apache.commons.lang.StringUtils;
import org.motechproject.whp.functional.framework.MyPageFactory;
import org.motechproject.whp.functional.page.admin.CreateCmfAdminPage;
import org.motechproject.whp.functional.page.admin.ListAllCmfAdminsPage;
import org.motechproject.whp.functional.page.admin.ListProvidersPage;
import org.motechproject.whp.functional.page.provider.ProviderPage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.motechproject.whp.functional.framework.WebDriverFactory.createWebElement;
import static org.openqa.selenium.By.id;

public abstract class Page {

    private Logger logger = LoggerFactory.getLogger(Page.class);
    protected WebDriver webDriver;
    private static final long MaxPageLoadTime = 30;
    private static final long RetryTimes = 5;
    private static final long RetryInterval = 5;
    protected WebDriverWait wait;
    protected WebDriverWait waitWithRetry;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, MaxPageLoadTime);
        this.waitWithRetry = new WebDriverWait(webDriver, RetryInterval);
        this.waitForPageToLoad();
    }

    @FindBy(how = How.ID, using = "patientId")
    private WebElement searchBox;

    @FindBy(how = How.ID, using = "searchPatient")
    private WebElement searchButton;

    @FindBy(how = How.ID, using = "patientSearchError")
    private WebElement errorDiv;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private WebElement logoutLink;

    protected abstract void waitForPageToLoad();

    public void postInitialize() {
    }

    public LoginPage logout() {
        createWebElement(logoutLink).click();
        waitUntilElementIsNotPresent(By.linkText("Logout"));
        return getLoginPage(webDriver);
    }

    protected void waitForElementWithIdToLoad(final String id) {
        waitForElementToLoad(By.id(id));
    }

    protected void waitForElementWithXPATHToLoad(final String path) {
        waitForElementToLoad(By.xpath(path));
    }

    protected void waitForElementWithCSSToLoad(final String className) {
        waitForElementToLoad(By.className(className));
    }

    protected void waitForElementToLoadWithRetry(final By by) {
        for (int i = 1; i <= RetryTimes; i++) {
            try {
                Boolean foundElement = waitWithRetry.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return webDriver.findElement(by) != null;
                    }
                });
                if (foundElement) break;
            } catch (WebDriverException e) {
                logger.info(String.format("Retried %s time(s) ...", i));
                if (i == RetryTimes)
                    throw e;
            }
            webDriver.get(webDriver.getCurrentUrl());
        }
    }

    protected WebElement safeFindElement(By by) {
        try {
            return webDriver.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected WebElement safeFindElementIn(WebElement webElement, By by) {
        try {
            return webElement.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected void waitUntilElementEditable(final By by) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(by).isEnabled() && webDriver.findElement(by).isDisplayed();
            }
        });
    }

    protected void waitUntilElementIsNotPresent(final By by) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return safeFindElement(by) == null;
            }
        });
    }

    private void waitForElementToLoad(final By by) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(by) != null;
            }
        });
    }

    protected void waitForElementToBeReloadedByAjax() {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return (Boolean) ((JavascriptExecutor) webDriver).executeScript("return jQuery.active == 0");
            }
        });
    }

    protected void waitForSuccess(String operation) {
        System.out.println("START: " + operation + " - Wait for success ...................................");
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                WebElement statusMessage = webDriver.findElement(id("statusMessage"));
                System.out.println(statusMessage.getText());
                return StringUtils.containsIgnoreCase(statusMessage.getText(), "success");
            }
        });
        System.out.println("END: " + operation + " - Wait for success ...................................");
        System.out.println("***************************************************************************");
    }

    public static LoginPage getLoginPage(WebDriver webDriver) {
        return MyPageFactory.initElements(webDriver, LoginPage.class);
    }

    public static ProviderPage getProviderPage(WebDriver webDriver) {
        return MyPageFactory.initElements(webDriver, ProviderPage.class);
    }

    public static CreateCmfAdminPage getCreateCmfPage(WebDriver webDriver) {
        return MyPageFactory.initElements(webDriver, CreateCmfAdminPage.class);
    }

    public static ListAllCmfAdminsPage getListAllCmfAdminsPage(WebDriver webDriver) {
        return MyPageFactory.initElements(webDriver, ListAllCmfAdminsPage.class);
    }
    public static ListProvidersPage getListProvidersPage(WebDriver webDriver) {
        return MyPageFactory.initElements(webDriver, ListProvidersPage.class);
    }

}
