Feature: Three-Way Matching Invoice Reconciliation (CU3)
  As an Accounts Payable Clerk
  I want to verify invoice consistency against PO and NIR
  So that payments are authorized only for ordered and received goods

  Scenario: Successful reconciliation within tolerance limit
    Given the three-way matching reconciliation parameters:
      | Ordered Quantity | NIR Quantity | Invoiced Quantity | PO Price | Invoice Price |
      | 100.0            | 100.0        | 100.0             | 50.0 EUR | 50.0 EUR      |
    When the finance department triggers three-way reconciliation
    Then the invoice is approved for payment
    And the goods receipt note status becomes "Reconciliat"

  Scenario: Block invoice due to price discrepancy exceeding tolerance
    Given the three-way matching reconciliation parameters:
      | Ordered Quantity | NIR Quantity | Invoiced Quantity | PO Price | Invoice Price |
      | 100.0            | 100.0        | 100.0             | 50.0 EUR | 65.0 EUR      |
    When the finance department triggers three-way reconciliation
    Then the invoice is blocked with status "Blocata"
    And the goods receipt note status becomes "CuDiscrepante"
