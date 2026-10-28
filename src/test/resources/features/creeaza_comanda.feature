Feature: Create and Validate Purchase Order (CU1)
  As a Procurement Officer
  I want to place a purchase order with an approved supplier
  So that raw materials stock is replenished in the warehouse

  Scenario: Successful order placement for approved supplier
    Given the following order details:
      | Supplier | Supplier Status | Material | Quantity | UOM | Unit Price | Currency |
      | ARC-001  | Aprobat         | TABLA-01 | 50.0     | TO  | 120.0      | EUR      |
    When the officer places the order with delivery date in 20 days
    Then the order is registered with status "Solicitat"
    And a unique purchase order identifier is generated
    And the access is logged in the audit journal

  Scenario: Reject order placement for ineligible supplier
    Given the following order details:
      | Supplier | Supplier Status | Material | Quantity | UOM | Unit Price | Currency |
      | ARC-002  | Candidat        | TABLA-01 | 10.0     | TO  | 100.0      | EUR      |
    When the officer attempts to place the order
    Then the request is rejected with error "422 Unprocessable Entity"
    And no order is saved in the system
