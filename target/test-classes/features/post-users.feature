@allTests
Feature: This feature is validate create user endpoint to entry new user

  Scenario Outline: User calls the API to entry new user "<description>"
    Given I have provided information as below
      | name | <name> |
      | job  | <job>  |
    When I called the endpoint "<endpoint>"
    Then I should receive the status code is 201
    And should have expected return body

    @post
    Examples:
      | description                   | endpoint | name     | job    |
      | Create user with name and job | /users   | morpheus | leader |

    @post
    Examples: Below details must return 400 error code but API is storing all type of data which could corrupt database
      | description                           | endpoint | name            | job           |
      | Create user with spacial char in name | /users   | mor£$%^&@*pheus | leader        |
      | Create user with spacial char in job  | /users   | mor£$%^&@*pheus | le£$%^&@*ader |
      | Create user without name              | /users   |                 | leader        |
      | Create user with job                  | /users   |                 | leader        |
      | Create user without name and job      | /users   |                 |               |