package com.learnhub.community.controller;

import com.learnhub.community.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CRUD générique : GET, GET/{id}, POST, PUT/{id}, DELETE/{id}. */
public abstract class BaseCrudController<T> {

    protected abstract JpaRepository<T, String> repo();
    protected abstract void setId(T entity, String id);
    protected abstract String label();

    @GetMapping
    public List<T> findAll() { return repo().findAll(); }

    @GetMapping("/{id}")
    public T findOne(@PathVariable String id) {
        return repo().findById(id).orElseThrow(() -> new NotFoundException(label() + " introuvable : " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@Valid @RequestBody T body) {
        setId(body, null);
        return repo().save(body);
    }

    @PutMapping("/{id}")
    public T update(@PathVariable String id, @Valid @RequestBody T body) {
        if (!repo().existsById(id)) throw new NotFoundException(label() + " introuvable : " + id);
        setId(body, id);
        return repo().save(body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!repo().existsById(id)) throw new NotFoundException(label() + " introuvable : " + id);
        repo().deleteById(id);
    }
}
