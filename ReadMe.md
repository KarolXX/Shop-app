# Shop app 
Technologies:
* Spring Boot
* MySql as database for app and h2 as database for tests
* JPA - Hibernate 
* flyway for database migrations
* Mockito, JUnit, AssertJ for tests (spring-boot-starter-test dependency)

[Frontend of this backend app](https://github.com/KarolXX/Shop-app-frontend)

# Description of the application behaviour and rules
There are two entities:
* product 
* category
A product can be associated with a category (but does not have to be). Category must be associated with at least one product.

Layout screenshots of the two main components of the application:

![image](https://user-images.githubusercontent.com/71709330/163165470-ed67c715-6f12-4c3d-9693-f0bc2f6ed005.png)

![image](https://user-images.githubusercontent.com/71709330/163165579-fc75beb0-0422-4b89-97e7-81a72a36bdb4.png)

## Product
Properties: name, amount, active, category
#### Some rules related to product:
* When fetching all products, we are fetching those whose amount > 0
* The fact that amount == 0 does not mean that active == FALSE - these two properties are independent of each other, the active property is needed when removing a product (more on that below)
* Removal of the product does not result in immediate removal from DB, results in setting the active flag to FALSE and complete removal takes place after 60 seconds - during these 60 seconds, the application should be able to recover the removed product (setting the active flag to TRUE) 
* Removal of a product associated with a category does not remove a category UNLESS this category has no other related products (then the category ceases to be associated with its only product, which is prohibited, so the category should be removed)

## Category
Properties: name, totalQuantity (sum of the amounts of all related products), products
#### some rules related to category:
* If the amount property changes in one of the associated products then the totalQuantity property should be changed accordingly. The same is true for deletion and possible restoration of a product - this should be reflected in the totalQuantity property
* Removing a category does not remove its related products. This only breaks the association (setting the category property to NULL)


# Current lacks of app:
* Unable to register users




