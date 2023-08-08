# own-library
ENG: The project is based on Java and Spring Boot and implements the accounting of books in the (home) library. 

This application is developed as a monolithic product, where the MVC methods of the controller directly access the data repository.
The application uses PostgreSQL databases, Spring Boot Data Jpa, Spring Boot Security, etc. dependencies.
The application implements the following features:
1. user registration with the creation of an account with a password and assigned access roles;
2. user authentication using a username and password, as well as access to application functions, depending on the assigned roles;
3. keeping records of books (Book) with links to the list of authors (List<Author>), genre (Genre) and library fund (Fund);
4. maintaining a list of books taken by the user for reading, with the choice of the status of the book: "reading" and "returned";
5. using the theme to design the application interface (themes are created as css files) and interactively switch to another application interface when changing the theme in the user settings;
6. select and save personal settings for the current user, such as:
a) application design theme: standard - light and dark - dark (when changing the theme, an interactive change of the application interface occurs),
b) the number of entries displayed when paginating lists;
7. saving a chain of user transitions in the application interface (in the form of linked URIs) with the ability to navigate through the saved chain of transitions "forward" (to the end) and "backward" (to the beginning).

/* -------------------------------------------------------------------------------------------------------------- */

RUS: Проект основан на Java и Spring Boot и реализует учет книг в (домашней) библиотеке. 

Данное приложение разработано в виде монолитного продукта, где методы MVC контроллера напрямую обращаются в репозиторий данных. 
В приложении используются БД PostgreSQL, Spring Boot Data Jpa, Spring Boot Security и др. зависимости. 
В приложении реализованы возможности: 
1. регистрации пользователя с созданием учетной записи с паролем и назначенными ролями доступа;
2. аутентификации пользователя с помощью имени и пароля, а также доступа к функциям приложения в зависимости от назначенных ролей; 
3. ведения учета книг (Book) со ссылками на список авторов (List<Author>), жанр (Genre) и библиотечный фонд (Fund); 
4. ведения списка книг, взятых пользователем для чтения, с выбором статуса книги: "читаю" и "вернул"; 
5. использования темы для оформления интерфейса приложения (темы созданы в виде css файлов) и интерактивно переключаться на другой интерфейс приложения при изменении темы в настройках пользователя; 
6. выбирать и сохранять текущему пользователю персональные настройки, такие как: 
a) тема оформления приложения: standard - светлая и dark - темная (при изменении темы происходит интерактивное изменение интерфейса приложения),
b) количество отображаемых записей при постраничном просмотре списков; 
7. сохранения цепочки переходов пользователя в интерфейсе приложения (в виде связанных URI) с возможностью навигации по сохраненной цепочке переходов "вперед" (в конец) и "назад" (в начало). 
