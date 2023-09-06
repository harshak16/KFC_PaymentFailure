Feature: Verify payment failure for KFC order


  @KFC_PaymentFailure
  Scenario: Verify the payment failure scenario during checkout - Pickup
    Given the user is on the KFC website
    When the user clicks on the 'Start oder' button
    And user selects order type as 'Pick up'
    And user selects 'Northmead' as the pickup location
    And user clicks on 'View menu'
    Then user selects 'Bucket for One' to the cart
    And the user clicks on 'Checkout'
    Then user checkouts as 'Guest'
    And user enters 'Firstname', 'Lastname', '0400000000', 'testemai@test.com' on the checkout page
    Then user continues to the payment method using 'Card' option
    Then Verify the error message for the failed payment

