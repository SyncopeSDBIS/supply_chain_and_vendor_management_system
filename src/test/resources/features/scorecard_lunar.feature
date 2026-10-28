Feature: Monthly Supplier Performance Scorecard (Q3)
  As a Quality Manager
  I want automated monthly supplier scorecard evaluation
  So that supplier qualification levels reflect actual performance

  Scenario: Automated downgrade to Conditionat on grade C
    Given the monthly performance indicators:
      | OTIF | Rejection Rate | Price Compliance | Weighted Score | Grade |
      | 70%  | 10%            | 90%              | 74.0           | C     |
    When the system evaluates monthly scorecard for an "Aprobat" supplier
    Then the supplier status becomes "Conditionat"

  Scenario: Rehabilitation after 3 consecutive months with grade B or A
    Given a supplier in status "Conditionat"
    When the supplier achieves grade B in consecutive months 1, 2, and 3
    Then the supplier status automatically returns to "Aprobat"
