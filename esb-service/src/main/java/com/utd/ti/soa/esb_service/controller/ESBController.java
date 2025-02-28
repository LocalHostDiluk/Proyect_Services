package com.utd.ti.soa.esb_service.controller;


import javax.websocket.server.PathParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.utils.Auth;

@RestController
@RequestMapping("/app/esb")
public class ESBController {
    private final WebClient webClient = WebClient.create();
    private final Auth auth = new Auth();

    //Crear usuario
    @PostMapping("/user")
    public ResponseEntity createUser (@RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        System.out.println("Request Body: " + user);
        System.out.println("Token recibido: " + token);

        //Validar token
        if (!auth.validateToken(token)){
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }
        
        //Enviar petición al servicio de usuarios
        String response = webClient.post()
            .uri("http://localhost:5000/app/users/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
        
        return ResponseEntity.ok(response);
    }

    //Obtener todos los usuarios
    @GetMapping("/user")
    public ResponseEntity getUser (@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        System.out.println("Token recibido: " + token);
        
        if (!auth.validateToken(token)){
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("http://localhost:5000/app/users/all")
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
        
        return ResponseEntity.ok(response);
    }

    // Actualizar usuario
    @PatchMapping("/user/update/{id}")
    public ResponseEntity updateUser(@PathVariable String id,
            @RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + user);
        System.out.println("Token recibido: " + token);
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.patch() // Usamos PATCH en lugar de POST
            .uri("http://localhost:5000/app/users/update/" + id) // Coincide con la ruta del backend
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    // Eliminar usuario
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para eliminar: " + id);
        System.out.println("Token recibido: " + token);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.delete() // Usamos DELETE en lugar de POST
            .uri("http://localhost:5000/app/users/delete/" + id) // Coincide con la ruta del backend
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

}