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

    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }



    public String createUser(String userName, String mobile) throws Exception{

        if (userMobile.contains(mobile)){
            throw new Exception ("User already exists");
        }
        else {
            userMobile.add(mobile);
//            userHashMap.put(userName,user);
            User user = new User(userName, mobile);
            return "SUCCESS";
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
        if (adminMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            boolean flag = false;
            for (User user: users){
                if (sender.equals(user)) {
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
                return msg.size();
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
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
        return "SUCCESS";
    }


    public int removeUser(User user) throws Exception {
        int deletedMessages = 0;
        int updatedUsers = 0;
        int updatedNoOfMessages =0;
        for (Map.Entry<Group,List<User>> map: groupUserMap.entrySet()) {
            List<User> users = map.getValue();
            boolean flag = false;

            for (User user1: users){
                if (user1.equals(user)){
                    flag=true;
                    break;
                }
            }
            if (flag){
                if (adminMap.containsKey(user)){
                    throw new Exception("Cannot remove admin");
                }
                else {
                    //Main logic of deletion starts here
                    users.remove(user);
                    updatedUsers = users.size();
                    if (groupMessageMap.containsKey(user)){
                        updatedUsers = groupMessageMap.get(user).size() - deletedMessages;
                        messageId-=groupMessageMap.get(user).size();
                        groupMessageMap.remove(user);

                    }
                }

            }
            else{
                throw new Exception("User not found");
            }
        }
        return (updatedUsers+ updatedUsers + messageId);
    }


    public String findMessage(Date start, Date end, int k) throws Exception{
        List<Message> messages = new ArrayList<>();
        return null;
    }
}
