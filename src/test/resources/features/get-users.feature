@allTests
Feature: This feature is to get User details

  Scenario Outline: User calls the get user endpoint "<description>"
    Given I am calling GET user endpoint to fetch data with "<id>"
    When I called get the endpoint "<endpoint>" with "<per_page>"
    Then I should receive the status code is 200
    And should return data based on page

    @get
    Examples:
      | description                              | endpoint | id  | per_page |
      | get response with default parameters     | /users   | all | default  |
      | get response with high value in per_page | /users   | all | max      |
      | get response with 1 per page             | /users   | all | 1        |

  Scenario Outline: User calls the get user endpoint "<description>"
    Given I am calling GET user endpoint to fetch data with "<id>"
    When I called get the endpoint "<endpoint>"
    Then I should receive the status code is 200
    And should return the user details associated with the id

    @get
    Examples:
      | description   | endpoint | id |
      | get all users | /users   | 3  |

  Scenario Outline: User calls the get user endpoint "<description>"
    Given I am calling GET user endpoint to fetch data with "<id>"
    When I called get the endpoint "<endpoint>"
    Then I should receive the status code is 404

    @get
    Examples:
      | description                        | endpoint | id   |
      | get all users with non existing id | /users   | 189  |
      | get all users with invalid id      | /users   | 3*5  |
      | get all users with invalid id      | /users   | 3*5& |