Feature: Cascade Cancellation of Purchase Order and Unreceived Lots (UC4)
  As a Procurement Officer
  I want canceling an order to atomically cancel all unreceived lots
  So that inventory and transit consistency is maintained

  Scenario: Cancel order with partially received lots
    Given a purchase order with 2 planned lots:
      | Lot   | Quantity | Initial State   |
      | LOT-1 | 40 TO    | SositInDepozit  |
      | LOT-2 | 60 TO    | InTranzit       |
    When the procurement officer cancels the order
    Then the order transitions to status "Anulat"
    And lot "LOT-1" remains in state "SositInDepozit"
    And lot "LOT-2" transitions to state "Anulat"
