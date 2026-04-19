package org.example;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.ScrollIntoViewOptions.instant;
import static com.codeborne.selenide.Selenide.*;
import static org.testng.Assert.assertNotNull;

@Feature("IMDB")
public class ImdbSearchTest {

    // page content
    private static final String PAGE_CONTAINER = ".ipc-page-content-container";
    private static final String HERO_PRIMARY_TEXT = "[data-testid='hero__primary-text']";

    // cookie consent banner
    private static final String CONSENT_BANNER = "[data-testid='consent-banner']";
    private static final String CONSENT_ACCEPT = "[data-testid='accept-button']";

    // search
    private static final String SEARCH_INPUT = "#suggestion-search";
    private static final String SUGGESTIONS_LISTBOX = "[role='listbox']";
    private static final String SUGGESTION_ITEM = ".react-autosuggest__suggestion";
    private static final String SUGGESTION_TITLE = ".searchResult__constTitle";
    private static final String SUGGESTION_TITLE_LINK = "a[href*='/title/']";

    // cast section
    private static final String CAST_CONTAINER = "[data-testid='shoveler-items-container']";
    private static final String CAST_ITEM = "[data-testid='title-cast-item']";
    private static final String CAST_ACTOR_LINK = "[data-testid='title-cast-item__actor']";

    private static final int MIN_EXPECTED_CAST = 3;

    @BeforeClass
    public void setUp() {
        Configuration.browser = "firefox";
        Configuration.baseUrl = "https://www.imdb.com";
        Configuration.headless = false;
    }

    @BeforeMethod
    public void beforeEach() {
        openHomePage();
        dismissCookieConsentIfPresent();
    }

    @Test
    @Description("Search for a movie, navigate to its page, then verify a cast member's profile")
    public void homeworkTaskFullFlow() {

        // full flow of the given task:
        /*
            Open imdb.com
            Search for "QA" with the search bar
            When dropdown opens, save the name of the first title
            Click on the first title
            Verify that page title matches the one saved from the dropdown
            Verify there are more than 3 members in the "top cast section"
            Click on the 3rd profile in the "top cast section"
            Verify that correct profile have opened
         */

        typeInSearchBox("QA");
        $(SUGGESTIONS_LISTBOX).shouldBe(visible);

        ElementsCollection suggestions = $$(SUGGESTION_ITEM);
        suggestions.shouldHave(sizeGreaterThan(0));

        // Find the first suggestion that has both a title and a link to a title page
        String savedTitle = null;
        SelenideElement savedLink = null;
        for (SelenideElement suggestion : suggestions) {
            SelenideElement title = suggestion.$(SUGGESTION_TITLE);
            SelenideElement link = suggestion.$(SUGGESTION_TITLE_LINK);
            if (title.exists() && !title.getText().isBlank() && link.exists()) {
                savedTitle = title.getText();
                savedLink = link;
                break;
            }
        }
        assertNotNull(savedTitle, "No suggestion with a title was found");

        savedLink.scrollIntoView(instant()).click();
        $(HERO_PRIMARY_TEXT).shouldBe(visible).shouldHave(exactText(savedTitle));

        // Navigate to the third cast member's profile page
        $(CAST_CONTAINER).shouldBe(visible);
        $$(CAST_ITEM).shouldHave(sizeGreaterThan(MIN_EXPECTED_CAST));

        SelenideElement actorLink = $$(CAST_ITEM).get(2).$(CAST_ACTOR_LINK);
        String actorName = actorLink.getText();

        actorLink.scrollIntoView(instant()).click();
        $(HERO_PRIMARY_TEXT).shouldBe(visible).shouldHave(exactText(actorName));
    }

    @Test
    @Description("Type a query in the search bar and verify that autocomplete suggestions appear")
    public void searchAutocompleteSuggestions() {
        typeInSearchBox("QA");
        $(SUGGESTIONS_LISTBOX).shouldBe(visible);
        $$(SUGGESTION_ITEM).shouldHave(sizeGreaterThan(0));
    }

    @Test
    @Description("Select a suggestion and verify the title page matches")
    public void searchSuggestionNavigatesToTitlePage() {
        typeInSearchBox("QA");
        $(SUGGESTIONS_LISTBOX).shouldBe(visible);

        ElementsCollection suggestions = $$(SUGGESTION_ITEM);
        suggestions.shouldHave(sizeGreaterThan(0));

        String savedTitle = null;
        SelenideElement savedLink = null;
        for (SelenideElement suggestion : $$(SUGGESTION_ITEM)) {
            SelenideElement title = suggestion.$(SUGGESTION_TITLE);
            SelenideElement link = suggestion.$(SUGGESTION_TITLE_LINK);
            if (title.exists() && !title.getText().isBlank() && link.exists()) {
                savedTitle = title.getText();
                savedLink = link;
                break;
            }
        }
        assertNotNull(savedTitle, "No suggestion with a title name was found");

        savedLink.scrollIntoView(instant()).click();
        $(HERO_PRIMARY_TEXT).shouldBe(visible).shouldHave(exactText(savedTitle));
    }

    @Test
    @Description("Open The Q&A title page and verify the film title is present")
    public void filmDetailsPageShowsTitleName() {
        openTitlePage("tt0100442");
        $(HERO_PRIMARY_TEXT).shouldBe(visible);
    }

    @Test
    @Description("Open a title page and navigate to a cast member's profile")
    public void titlePageCastMemberNavigation() {
        openTitlePage("tt0100442");

        $(CAST_CONTAINER).shouldBe(visible);
        $$(CAST_ITEM).shouldHave(sizeGreaterThan(MIN_EXPECTED_CAST));

        SelenideElement actorLink = $$(CAST_ITEM).get(2).$(CAST_ACTOR_LINK);
        String actorName = actorLink.getText();

        actorLink.scrollIntoView(instant()).click();
        $(HERO_PRIMARY_TEXT).shouldBe(visible).shouldHave(exactText(actorName));
    }

    @Step("Open IMDB homepage")
    private void openHomePage() {
        open("/");
        $(PAGE_CONTAINER).shouldBe(visible);
    }

    @Step("Dismiss cookie consent banner if present")
    private void dismissCookieConsentIfPresent() {
        if ($(CONSENT_BANNER).is(visible)) {
            $(CONSENT_ACCEPT).click();
        }
    }

    @Step("Type '{query}' in the search box")
    private void typeInSearchBox(String query) {
        $(SEARCH_INPUT).setValue(query);
    }

    @Step("Open title page for '{titleId}'")
    private void openTitlePage(String titleId) {
        open("/title/" + titleId);
    }
}
