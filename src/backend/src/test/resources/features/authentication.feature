Feature: Authentification Utilisateur
  En tant qu'utilisateur du site e-commerce
  Je veux m'inscrire et me connecter
  Afin de pouvoir passer des commandes

  Scenario: Inscription réussie d'un nouvel utilisateur
    Given Le système est prêt pour une nouvelle inscription
    When J me fais une inscription avec le nom "Cucumber", le prénom "Tester" et l'email "cucumber@test.com"
    Then Le code de réponse de l'inscription doit être 201

  Scenario: Connexion réussie avec des identifiants valides
    Given Un utilisateur existe avec l'email "cucumber@test.com" et le mot de passe "password123"
    When Je tente de me connecter avec l'email "cucumber@test.com" et le mot de passe "password123"
    Then Le code de réponse doit être 200
    And Une réponse contenant un token JWT doit être retournée
