package net.devlord.trendy.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "The page you are looking for doesn't exist.");
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "You don't have permission to access this resource.");
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
                if (errorMessage != null) {
                    model.addAttribute("error", errorMessage.toString());
                } else {
                    model.addAttribute("error", "An unexpected error occurred.");
                }
                return "error/500";
            }
        }
        
        // Default to 500 error
        model.addAttribute("error", "An unexpected error occurred.");
        return "error/500";
    }
}

