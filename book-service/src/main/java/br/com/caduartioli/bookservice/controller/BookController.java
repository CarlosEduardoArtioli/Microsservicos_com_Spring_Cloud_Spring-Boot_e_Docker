package br.com.caduartioli.bookservice.controller;

import br.com.caduartioli.bookservice.model.Book;
import br.com.caduartioli.bookservice.proxy.CambioProxy;
import br.com.caduartioli.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @Autowired
    CambioProxy cambioProxy;

    @GetMapping(value = "/{id}/{currency}")
    public Book findBook(
            @PathVariable("id") Long id,
            @PathVariable("currency") String currency
    ) {
        var book = repository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        var cambio = cambioProxy.getCambio(BigDecimal.valueOf(book.getPrice()), book.getCurrency(), currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment(port);
        book.setPrice(cambio.getConvertedValue().doubleValue());

        return book;
    }
}
