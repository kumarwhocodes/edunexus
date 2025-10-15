package dev.kumar.edunexus.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import dev.kumar.edunexus.exception.FirebaseOperationException;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Log4j2
public class FirebaseUtil {
    
    public Optional<UserRecord> fetchUserFromToken(String token) {
        String uid = extractUidFromToken(token);
        try {
            return Optional.of(FirebaseAuth.getInstance().getUser(uid));
        } catch (FirebaseAuthException e) {
            log.error("Error fetching Firebase user: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    public String extractUidFromToken(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            log.error("Error decoding token: {}", e.getMessage());
            throw new ResourceNotFoundException("Invalid token");
        }
    }
    
    public void deleteUser(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
        } catch (FirebaseAuthException e) {
            throw new FirebaseOperationException("Failed to delete user from Firebase: " + e.getMessage());
        }
    }
}