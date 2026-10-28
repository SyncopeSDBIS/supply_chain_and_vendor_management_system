Feature: Quality Inspection and Acceptance Sampling (CU2)
  As a Quality Inspector
  I want to inspect lot samples according to ISO 2859-1 AQL
  So that only conforming goods enter operational inventory

  Scenario: Conforming verdict when defects are within acceptance limit
    Given the following AQL inspection parameters:
      | Sample Size | Acceptance Number | Rejection Number | Defects Found |
      | 80          | 3                 | 4                | 2             |
    When the inspector records the inspection verdict
    Then the inspection is marked as "Conforma"
    And the lot is released into operational stock

  Scenario: Nonconforming verdict generates automated NCR
    Given the following AQL inspection parameters:
      | Sample Size | Acceptance Number | Rejection Number | Defects Found |
      | 80          | 3                 | 4                | 5             |
    When the inspector records the inspection verdict
    Then the inspection is marked as "Neconforma"
    And a nonconformance report NCR is automatically opened
    And the lot is placed in state "Respins"
