package com.mycompany;

import com.mycompany.tenant.TenantContext;
import com.mycompany.tenant.TenantIdentifierResolver;
import com.mycompany.entity.Book;
import com.mycompany.entity.User;
import com.mycompany.repository.BookRepository;
import com.mycompany.repository.UserRepository;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(
        exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class
        }
)
public class Application implements CommandLineRunner {

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriver;

    @Value("${spring.jpa.database-platform}")
    private String jpaDialect;

    @Value("${spring.jpa.database-platform}")
    private String jpaDDL;

    @Autowired
    private UserRepository users;

    @Autowired
    private BookRepository books;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        setupUsers();

        var book1 = new Book();
        book1.setTitle("O Código Da Vinci");

        setupBook("db1", book1);

        var book2 = new Book();
        book1.setTitle("Anjos e Demônios");

        setupBook("db2", book2);
    }

    private void setupUsers() {

        setupTable(TenantIdentifierResolver.DEFAULT_TENANT_ID, User.class);

        var list = users.findAll();

        if (!list.isEmpty()) {
            return;
        }

        var user1 = new User();
        user1.setName("Joao");
        user1.setEmail("joao@me.com");
        user1.setPassword("jo@123qwe");
        user1.setTenantId("db1");

        users.save(user1);

        var user2 = new User();
        user2.setName("Maria");
        user2.setEmail("maria@me.com");
        user2.setPassword("ma@123qwe");
        user2.setTenantId("db2");

        users.save(user2);
    }

    private void setupBook(String tenantId, Book book) {

        TenantContext.setTenantId(tenantId);

        setupTable(tenantId, book.getClass());

        var list = books.findAll();

        if (!list.isEmpty()) {
            return;
        }

        books.save(book);
    }

    public void setupTable(String tenantId, Class cl) {

        var cfg = new Configuration()
                .setProperty(Environment.URL, "jdbc:sqlite:sqlite/" + tenantId + ".db")
                .setProperty(Environment.USER, datasourceUsername)
                .setProperty(Environment.PASS, datasourcePassword)
                .setProperty(Environment.DRIVER, datasourceDriver)
                .setProperty(Environment.DIALECT, jpaDialect)
                .setProperty(Environment.HBM2DDL_AUTO, jpaDDL);

        cfg.addAnnotatedClass(cl);

        var factory = cfg.buildSessionFactory();

        try (var session = factory.openSession()) {
            session.close();
        }
    }

}
