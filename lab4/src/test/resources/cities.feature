Feature: Cities game
  
  Scenario: game
    Given I have my cities game
    
  Scenario Outline: city exists
    Given I have my cities game
    When I entered "<name>" as city name
    And I check does this city exist
    Then The result should be <result>
    
    Examples:
      | name    | result |
      | Барнаул | true   |
      | Москва  | true   |
      | Фывыпро | false  |
      | НовоСибирск | true |
    
  Scenario Outline: last character
    Given I have my cities game
    When I entered "<name>" as city name
    Then The last character should be <char>
    
    Examples:
      | name | char |
      | Волгоград | д |
      | ЯлтА      | а |
      | Тюмень    | н |
      | Агайры    | р |
      | Ажбай     | а |
    
  Scenario Outline: follow
    Given I have my cities game
    When Other player entered "<other>" as city name
    And I entered "<name>" as city name
    And I want to check does my answer valid 
    Then The result should be <result>
    
    Examples:
      | other | name | result |
      | Новосибирск | Кемерово | true |
      | Барнаул     | Москва   | false |
      | Тюмень      | Нижневартовск | true |
    
  Scenario Outline: already used
    Given I have my cities game
    When Current player answered "<name>"
    Then Should not be thrown exception
    And Current player answered "<other>"
    Then Should not be thrown exception
    And Current player answered "<check>"
    Then Should be thrown exception with message contains "answered"
    
    Examples:
      | name | other | check |
      | Омск | Кемерово | Омск |
      | Бийск  | Красноярск | Красноярск |
    
  Scenario: valid sequence
    Given I have my cities game
    When Current player answered "Барнаул"
    Then Should not be thrown exception
    And Current player answered "Лосево"
    Then Should not be thrown exception
    And Current player answered "Омск"
    Then Should not be thrown exception
    
  Scenario: wrong character
    Given I have my cities game
    When Current player answered "Новосибирск"
    Then Should not be thrown exception
    And Current player answered "Бийск"
    Then Should be thrown exception with message contains "wrong"
    
  Scenario: wrong city
    Given I have my cities game
    When Current player answered "Москва"
    Then Should not be thrown exception
    And Current player answered "Аккыык"
    Then Should be thrown exception with message contains "not found"
    
  Scenario: current player
    Given I have my cities game
    Then Current player should be 1
    When Current player answered "Москва"
    Then Current player should be 2
    And Current player answered "Аропаккузи"
    Then Current player should be 1
    
  Scenario: time check
    Given I have my cities game
    Given Delay is 200 ms
    When Current player answered "Бийск"
    Then Should not be thrown exception
    And Current player wait 220 ms
    And Current player answered "Кемерово"
    Then Should be thrown exception with message contains "over"
    
  Scenario: interface
    Given I have interface for my game
    