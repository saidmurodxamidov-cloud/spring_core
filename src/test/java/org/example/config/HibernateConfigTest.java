package org.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HibernateConfigTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TransactionConfig transactionConfig; // your @Configuration class

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(false);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    }

    @Test
    void testDataSourceConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void testEntityManagerFactory() {
        EntityManager em = entityManagerFactory.createEntityManager();
        assertNotNull(em);
    }

    @Test
    void testTransactionManagerCreation() {
        JpaTransactionManager txManager = transactionConfig.transactionManager(entityManagerFactory);
        assertNotNull(txManager);
    }
}
