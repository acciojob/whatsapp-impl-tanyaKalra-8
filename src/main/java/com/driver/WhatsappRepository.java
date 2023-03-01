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

//    private HashMap<String, User> userHashMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
//        this.userHashMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }



    public String createUser(String userName, String mobile) throws Exception{
        User user = new User(userName, mobile);
        if (userMobile.contains(mobile)){
            throw new Exception ("User already exists");
        }
        else {
            userMobile.add(mobile);
//            userHashMap.put(userName,user);
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


    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if (groupUserMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            boolean flag = false;
            for (User user: users){
                if (sender.equals(user.getName())) {
                    flag = true;
                    break;
                }
            }
            if (flag){
                messageId++;
                List<Message> msg = groupMessageMap.getOrDefault(group, new ArrayList<>());
                msg.add(message);
                groupMessageMap.put(group,msg);

                senderMap.put(message,sender);
            }
            else {
                throw new Exception("You are not allowed to send message");
            }
        }
        else {
            throw new Exception("Group does not exist");
        }
        return messageId;
    }


    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if (adminMap.containsKey(group)){
            if (adminMap.get(group).equals(approver)){
                List<User> users = groupUserMap.get(group);
                boolean flag = false;
                for (User user1: users){
                    if (user1.equals(user)){
                        flag = true;
                        break;
                    }
                }
                if (flag){
                    adminMap.remove(group);
                    adminMap.put(group,user);
                }
                else {
                    throw new Exception("User is not a participant");
                }
            }
            else {
                throw new Exception("Approver does not have rights");
            }
        }
        else {
            throw new Exception("Group does not exist");
        }
        return "Admin changed successfully";
    }


}
