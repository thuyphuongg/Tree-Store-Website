package com.cdweb.Treestore.services.implement;

import com.cdweb.Treestore.convert.RoleConvert;
import com.cdweb.Treestore.convert.UserConvert;
import com.cdweb.Treestore.dto.RoleDto;
import com.cdweb.Treestore.dto.UserDto;
import com.cdweb.Treestore.entity.ConfirmationToken;
import com.cdweb.Treestore.entity.PasswordResetToken;
import com.cdweb.Treestore.entity.RoleEntity;
import com.cdweb.Treestore.entity.UserEntity;
import com.cdweb.Treestore.repository.ConfirmationTokenRepository;
import com.cdweb.Treestore.repository.PasswordResetTokenRepository;
import com.cdweb.Treestore.repository.RoleRepository;
import com.cdweb.Treestore.repository.UserRepository;
import com.cdweb.Treestore.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConvert userConvert;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleConvert roleConvert;
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;


    @Override
    public UserDto findByEmail(String email) {
        UserEntity userEntity = this.userRepository.findByEmailIgnoreCaseAndIsEnabled(email, true);
        if (userEntity != null) {
            return this.userConvert.toDTO(userEntity);
        }
        return null;
    }

    @Override
    public UserDto findEmail(String email) {
        UserEntity userEntity = this.userRepository.findByEmailIgnoreCase(email);
        if (userEntity != null) {
            return this.userConvert.toDTO(userEntity);
        }
        return null;
    }

    public UserDto sendMail(UserDto user, HttpServletRequest request) {
        UserEntity userEntity = new UserEntity();
        UserEntity existingUser = userRepository.findByEmailIgnoreCaseAndIsEnabled(user.getEmail(), true);
        if (existingUser != null) {
            return null;
        } else {
            UserEntity temp = userRepository.findByEmailIgnoreCaseAndIsEnabled(user.getEmail(), false);
            if (temp != null) {
                userRepository.delete(temp);
            }
            user.setPassword(encoder.encode(user.getPassword()));
            List<RoleDto> list = new ArrayList<>();
            RoleEntity role = roleRepository.findByName("ROLE_USER");
            list.add(roleConvert.toDTO(role));
            user.setRoleList(list);
            userEntity = userRepository.save(this.userConvert.toEntity(user));

            ConfirmationToken confirmationToken = new ConfirmationToken(userEntity);

            confirmationTokenRepository.save(confirmationToken);


            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("treestorenlu2023@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + baseUrl + "/confirm-account?token=" + confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);
            return userConvert.toDTO(userEntity);
        }
    }

    @Override
    public UserDto confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token != null) {
            UserEntity user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            UserEntity userEntity = userRepository.save(user);
            List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByUserId(token.getUser().getId());
            for (ConfirmationToken confirmToken : confirmationTokens) {
                confirmationTokenRepository.delete(confirmToken);
            }

            return userConvert.toDTO(userEntity);
        } else {
            return null;
        }
    }

    @Override
    public UserDto sendMailForgetPassword(String email, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        UserEntity user = this.userRepository.findByEmail(email);
        if (user == null) return null;
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        this.passwordTokenRepository.save(passwordResetToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Forget Password!");
        mailMessage.setFrom("treestorenlu2023@gmail.com");
        mailMessage.setText("To change your password, please click here : "
                + baseUrl + "/forget-password?token=" + passwordResetToken.getToken());
//                + "http://localhost:8080/forget-password?token=" + passwordResetToken.getToken());

        emailSenderService.sendEmail(mailMessage);
        return userConvert.toDTO(user);
    }

    @Override
    public UserDto confirmEmailForgetPassword(String token) {
        PasswordResetToken passwordResetToken = this.passwordTokenRepository.findByToken(token);
        if (passwordResetToken != null) {
            return userConvert.toDTO(passwordResetToken.getUser());
        } else {
            return null;
        }
    }

    @Override
    public UserDto changePassword(UserDto user) {
        UserEntity userEntity = this.userRepository.findByEmailIgnoreCase(user.getEmail());


        if (user.getFullName() != null) {
            userEntity.setFullName(user.getFullName());
        }
        if (user.getPhone() != null) {
            userEntity.setPhone(user.getPhone());
        }
        if (user.getAddress() != null) {
            userEntity.setAddress(user.getAddress());
        }
        if (user.isEnabled()) {
            userEntity.setEnabled(true);
        }
        if (user.getPassword() != "" && user.getPassword() != null) {
            userEntity.setPassword(encoder.encode(user.getPassword()));
        }
        if (user.getRoleList() != null && user.getRoleList().size() != 0) {
            userEntity.setRoleList(userEntity.getRoleList());
        }
        userEntity = this.userRepository.save(userEntity);
        if (user.getPassword() != "" && user.getPassword() != null) {
            if (userEntity != null) {
                List<PasswordResetToken> passwordResetTokens = this.passwordTokenRepository.findByUser(userEntity);
                if (passwordResetTokens.size() > 0) {
                    for (PasswordResetToken passwordResetToken : passwordResetTokens) {
                        this.passwordTokenRepository.delete(passwordResetToken);
                    }

                }
            }
        }
        return this.userConvert.toDTO(userEntity);
    }

    @Override
    public boolean checkPass(String password, String email) {
        UserEntity userEntity = this.userRepository.findByEmail(email);
        return encoder.matches(password, userEntity.getPassword());
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> listUser = new ArrayList<>();
        List<UserEntity> users = this.userRepository.findAll();
        for (UserEntity user : users) {
            listUser.add(this.userConvert.toDTO(user));
        }
        return listUser;
    }

    @Override
    public UserDto save(UserDto user) {
        return this.userConvert.toDTO(this.userRepository.save(this.userConvert.toEntity(user)));
    }

    @Override
    public void delete(String email) {
        this.userRepository.delete(this.userRepository.findByEmail(email));
    }
}