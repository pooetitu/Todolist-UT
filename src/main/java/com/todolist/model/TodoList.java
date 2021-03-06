package com.todolist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.todolist.service.EmailSenderService;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@JsonFormat(pattern = "dd/MM/YYYY")
@Table(name = "T_Todo_List")
public class TodoList {
    @Singular
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "todoList")
    private final List<Item> items = new ArrayList<>();

    @Id
    @GeneratedValue
    private long id;

    @NonNull
    @OneToOne
    @JsonBackReference
    @JoinColumn(name="user_id")
    private User user;

    @Transient
    private EmailSenderService emailSenderService = new EmailSenderService();

    public boolean addItem(Item item) {
        if (getItemsCount() < 10 &&
                item.isValid() &&
                !containsItemWithName(item.getName()) &&
                !lastInsertedItemInLastThirtyMinutes(item)) {
            items.add(item);
            item.setTodoList(this);
            if (getItemsCount() == 8) {
                emailSenderService.sendMail(user.getEmail());
            }
            return true;
        }
        return false;
    }

    public int getItemsCount() {
        return items.size();
    }

    public boolean containsItemWithName(String name) {
        return items.stream().anyMatch(it -> it.getName().equals(name));
    }

    public boolean lastInsertedItemInLastThirtyMinutes(Item itemToAdd) {
        Item item = items.stream().min(Comparator.comparing(Item::getCreationDate)).orElse(null);
        if (item == null) return false;
        long itemCreationTime = itemToAdd.getCreationDate().getTime() / 60000;
        long lastCreatedDate = item.getCreationDate().getTime() / 60000;
        return itemCreationTime - lastCreatedDate < 30;
    }
}
