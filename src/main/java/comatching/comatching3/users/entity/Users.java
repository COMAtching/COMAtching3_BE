package comatching.comatching3.users.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.charge.entity.ChargeRequest;
import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(
                name = "social_id_unique",
                columnNames = "social_id"
        )
})
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAiFeature userAiFeature;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointHistory> pointHistoryList = new ArrayList<PointHistory>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargeRequest> chargeRequestList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> orderList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    @OneToMany(mappedBy = "picker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomsPickedByMe = new ArrayList<>();

    @OneToMany(mappedBy = "picked", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomsWhoPickedMe = new ArrayList<>();

    private String socialId;

    private String provider;

    private String username;

    private String password;

    private String email;

    private String birthday;

    private String role;

    private String song;

    private String comment;

    private Long point = 0L;

    private Long payedPoint = 0L;

    @Setter
    private Long dailyPoint = 0L;

    @Setter
    private boolean make1000 = false;

    // 사용자가 신고한 목록 (내가 신고한 내역)
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsMade = new ArrayList<>();

    // 사용자가 신고당한 목록 (내가 신고된 내역)
    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsReceived = new ArrayList<>();


    private String schoolEmail;

    private boolean schoolAuth = false;

    private String contactId;

    private int warningCount = 0;

    private int pickedCount = 0;

    @Setter
    private String realName;

    @Builder
    public Users(String provider, String socialId, String email, String role, String username, String password) {
        this.provider = provider;
        this.socialId = socialId;
        this.email = email;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public List<ChatRoom> getAllChatRooms() {
        List<ChatRoom> all = new ArrayList<>();
        all.addAll(chatRoomsPickedByMe);
        all.addAll(chatRoomsWhoPickedMe);
        return all;
    }

    public void addNewOrder(Orders order) {
        orderList.add(order);
    }

    public void updateUserAiFeature(UserAiFeature userAiFeature) {
        this.userAiFeature = userAiFeature;
    }

    public void updateUniversity(University university) {
        this.university = university;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateSong(String song) {
        this.song = song;
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void addPoint(Long point) {
        this.point += point;
    }

    public void subtractPoint(Long point) {
        this.point -= point;
    }

    public void updateContactId(String contactId) {
        this.contactId = contactId;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void addPayedPoint(Long payedPoint) {
        this.payedPoint += payedPoint;
    }

    public void subtractPayedPoint(Long payedPoint) {
        this.payedPoint -= payedPoint;
    }

    public void addWarningCount() {
        this.warningCount += 1;
    }

    public void schoolAuthenticationSuccess() {
        this.schoolAuth = true;
    }

    public void updateSchoolEmail(String schoolMail) {
        this.schoolEmail = schoolMail;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void updatePickedCount() {
        this.pickedCount++;
    }

}
