package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;

    private HashMap<String, User> userHashMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.userHashMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }



    public String createUser(String userName, String mobile) {
        User user = new User(userName, mobile);
        if (userMobile.contains(mobile)){
            throw new RuntimeException("User already exists");
        }
        else {
            userMobile.add(mobile);
            userHashMap.put(userName,user);
            return "User added Successfully";
        }
    }

    public Group createGroup(List<User> users) {
        if (users.size()<2){
            return null;
        }
        else if (users.size()==2){
            Group group = new Group(users.get(1).getName(), users.size());
            adminMap.put(group, users.get(0));
            groupUserMap.put(group,users);
            return group;
        }
        else {
            customGroupCount++;
            Group group = new Group("Group "+ customGroupCount, users.size());
            adminMap.put(group, users.get(0));
            groupUserMap.put(group,users);
            return group;
        }
    }

    public int createMessage(String content) {
        messageId++;
        Message message = new Message(messageId,content);
        return messageId;
    }


    public int sendMessage(Message message, User sender, Group group) {
        if (groupUserMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            boolean flag = false;
            for (User user: users){
                if (sender.equals(user.getName())) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
                try {
                    throw new Exception("You are not allowed to send message");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                messageId++;
                List<Message> msg = groupMessageMap.getOrDefault(group, new ArrayList<>());
                msg.add(message);
                groupMessageMap.put(group,msg);

                senderMap.put(message,sender);
            }
        }
        else {
            try {
                throw new Exception("Group does not exist");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return messageId;
    }


}
