Feature: Payment Order Approval Workflow (I5, I6)
  As a Financial Officer
  I want high-value payment orders to require four-eyes approval
  So that financial risk and unapproved transactions are prevented

  Scenario: Payment order below threshold of 10000 EUR
    When a payment order of 5000 EUR is created
    Then the payment order is saved with status "Creat"
    And can be transmitted directly to the bank

  Scenario: Payment order above threshold of 10000 EUR requires four eyes
    When a payment order of 25000 EUR is created by User-A
    Then the payment order enters status "InAprobare"
    And cannot be approved by User-A
    When User-B approves the payment order
    Then the payment order transitions to status "Aprobat"
    And can now be transmitted to the bank
