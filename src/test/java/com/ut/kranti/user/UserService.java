package com.ut.kranti.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.ut.kranti.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ✅ Test getAllUsers
    @Test
    void testGetAllUsers() {
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setUsername("alice");
        user1.setEmail("alice@test.com");

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setUsername("bob");
        user2.setEmail("bob@test.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("alice");
        assertThat(result.get(1).getName()).isEqualTo("bob");
    }

    // ✅ Test saveUser
    @Test
    void testSaveUser() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("plainpassword");
        user.setEmail("john@test.com");

        when(userRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto saved = userService.saveUser(user);

        assertThat(saved.getName()).isEqualTo("john");
        assertThat(saved.getEmail()).isEqualTo("john@test.com");

        // Password should be encoded (not equal to plain text)
        assertThat(user.getPassword()).isNotEqualTo("plainpassword");
    }
}
