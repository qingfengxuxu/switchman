package de.is24.common.abtesting.service;

import de.is24.common.infrastructure.config.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;


@ActiveProfiles("test")
@IntegrationTest("server.port=0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LoginWT {
  private FirefoxDriver firefoxDriver;

  @Value("${local.server.port}")
  private String port;

  @Before
  public void setup() {
    firefoxDriver = new FirefoxDriver();
    firefoxDriver.get("http://localhost:" + port);
  }

  @Test
  public void shouldShowWelcomePage() {
    assertThat(firefoxDriver.getTitle(), is("A/B Test Service - Login"));
  }

  @Test
  public void shouldShowLoginErrorOnFalseLogin() {
    givenLoginPageAppearsWhenClickingOnManageDecisions();
    whenLoggingInWithWrongLogin();
    thenLoginErrorIsDisplayed();
  }

  @Test
  public void shouldShowDecisionsPageOnCorrectLogin() {
    givenLoginPageAppearsWhenClickingOnManageDecisions();
    whenLoggingInWithCorrectLogin();
    thenDecisionsAreDisplayed();
  }

  private void thenDecisionsAreDisplayed() {
    assertThat(firefoxDriver.findElementByClassName("panel-heading").getText(), is("AB Test Decisions- Search"));
  }

  private void whenLoggingInWithCorrectLogin() {
    login("admin", "admin");
  }

  private void whenLoggingInWithWrongLogin() {
    login("unknown", "no_idea");
  }

  private void thenLoginErrorIsDisplayed() {
    assertThat(firefoxDriver.findElementByClassName("alert-danger").getText(),
      startsWith("Benutzername oder Passwort"));
  }

  private void login(String userName, String passWord) {
    WebElement usernameInput = firefoxDriver.findElementById("username");
    usernameInput.sendKeys(userName);

    WebElement passwordInput = firefoxDriver.findElementById("password");
    passwordInput.sendKeys(passWord);

    WebElement loginButton = firefoxDriver.findElementById("login");
    loginButton.click();
  }

  private void givenLoginPageAppearsWhenClickingOnManageDecisions() {
    WebElement configurationsPageLink = firefoxDriver.findElement(By.id("decisions"));
    configurationsPageLink.click();
    assertThat(firefoxDriver.findElementByClassName("panel-title").getText(), is("Login"));
  }

  @After
  public void tearDown() {
    firefoxDriver.quit();
  }


}
