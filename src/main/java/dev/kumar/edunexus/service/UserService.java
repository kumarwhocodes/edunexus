package dev.kumar.edunexus.service;

import com.google.firebase.auth.UserRecord;
import dev.kumar.edunexus.dto.AccessTokenBody;
import dev.kumar.edunexus.dto.UserDTO;
import dev.kumar.edunexus.entity.User;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.mapper.UserMapper;
import dev.kumar.edunexus.repository.UserRepository;
import dev.kumar.edunexus.util.FirebaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository repo;
    private final UserMapper userMapper;
    private final FirebaseUtil firebaseUtil;
    
    // Login user with Firebase token
    public UserDTO loginUser(AccessTokenBody tokenBody) {
        String authHeader = tokenBody.getToken();
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        
        String token = authHeader.substring(7);
        UserRecord firebaseUser = firebaseUtil.fetchUserFromToken(token).orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        
        if (isUserPresent(firebaseUser))
            return userMapper.toDTO(fetchUserById(firebaseUser));
        else
            return createUser(firebaseUser);
    }
    
    // Fetch user by token
    public UserDTO fetchUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User user = repo.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }
    
    // Update user profile
    public UserDTO updateUser(String token, UserDTO userDTO) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uid));
        
        existingUser.setName(userDTO.getName());
        existingUser.setProfileUrl(userDTO.getProfileUrl());
        
        User updatedUser = repo.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }
    
    // Delete user account
    public void deleteUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uid));
        
        firebaseUtil.deleteUser(uid);
        repo.delete(existingUser);
    }
    
    
    private boolean isUserPresent(UserRecord firebaseUser) {
        return repo.existsById(firebaseUser.getUid());
    }
    
    private User fetchUserById(UserRecord firebaseUser) {
        return repo
                .findById(firebaseUser.getUid())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + firebaseUser.getUid()));
    }
    
    private UserDTO createUser(UserRecord firebaseUser) {
        User user = User
                .builder()
                .id(firebaseUser.getUid())
                .name(firebaseUser.getDisplayName())
                .email(firebaseUser.getEmail())
                .profileUrl(firebaseUser.getPhotoUrl())
                .build();
        
        repo.save(user);
        return userMapper.toDTO(user);
    }
}
