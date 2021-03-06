package com.todolist.unit;

import com.todolist.model.Item;
import com.todolist.model.TodoList;
import com.todolist.model.User;
import com.todolist.service.EmailSenderService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TodoListTest {
    private TodoList emptyTodoList;
    private TodoList fullTodoList;
    private TodoList oneItemTodoList;
    private TodoList sevenItemTodoList;
    private TodoList eightItemTodoList;
    private EmailSenderService emailSenderService;
    private Item validItem;
    private Item invalidItem;


    @Before
    public void setUp() {
        String email = "email@email.com";
        String uniqueItemName = "not-unique";
        User user = new User();
        user.setEmail(email);

        emailSenderService = mock(EmailSenderService.class);

        validItem = spy(new Item(uniqueItemName, "aaa"));
        when(validItem.isValid()).thenReturn(true);

        invalidItem = spy(new Item(uniqueItemName, "aaa"));
        when(invalidItem.isValid()).thenReturn(false);

        emptyTodoList = new TodoList();
        emptyTodoList.setUser(user);

        oneItemTodoList = spy(new TodoList());
        oneItemTodoList.addItem(new Item(uniqueItemName, "aaa"));
        when(oneItemTodoList.getItemsCount()).thenReturn(1);

        fullTodoList = spy(new TodoList());
        when(fullTodoList.getItemsCount()).thenReturn(10);
        fullTodoList.setUser(user);

        sevenItemTodoList = spy(new TodoList(user));
        when(sevenItemTodoList.getItemsCount()).thenReturn(7).thenReturn(8);
        sevenItemTodoList.setEmailSenderService(emailSenderService);

        eightItemTodoList = spy(new TodoList(user));
        when(eightItemTodoList.getItemsCount()).thenReturn(8);
        eightItemTodoList.setEmailSenderService(emailSenderService);
    }

    @Test
    public void insertInEmptyTodoListValidItem() {
        assertTrue(emptyTodoList.addItem(validItem));
        assertTrue(emptyTodoList.getItems().contains(validItem));
    }

    @Test
    public void insertInEmptyTodoListInvalidItem() {
        assertFalse(emptyTodoList.addItem(invalidItem));
        assertFalse(emptyTodoList.getItems().contains(invalidItem));
    }

    @Test
    public void numberOfItemIsNotFine() {
        Item item = new Item("11", "a");
        assertFalse(fullTodoList.addItem(item));
        assertFalse(fullTodoList.getItems().contains(item));
        assertTrue(fullTodoList.getItems().size() <= 10);
    }

    @Test
    public void insertAlreadyUsedNameItem() {
        assertFalse(oneItemTodoList.addItem(validItem));
        assertFalse(oneItemTodoList.getItems().contains(validItem));
    }

    @Test
    public void addBeforeCoolDown() {
        assertTrue(emptyTodoList.addItem(new Item("1", "a")));
        Item second = new Item("2", "a");
        assertFalse(emptyTodoList.addItem(second));
        assertFalse(emptyTodoList.getItems().contains(second));
    }

    @Test
    public void sendOneEmailAtEightItems() {
        assertTrue(sevenItemTodoList.addItem(validItem));
        verify(emailSenderService, times(1)).sendMail(anyString());
    }

    @Test
    public void sendNoMailOnInvalidInsert() {
        assertFalse(eightItemTodoList.addItem(invalidItem));
        verify(emailSenderService, never()).sendMail(anyString());
    }
}