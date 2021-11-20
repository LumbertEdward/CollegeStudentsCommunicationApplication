package com.example.tuchatapplication.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tuchatapplication.models.Chat
import com.example.tuchatapplication.models.Group
import com.example.tuchatapplication.models.GroupDisplay
import com.example.tuchatapplication.models.Member
import com.example.tuchatapplication.reporitories.ChattingRepository
import com.example.tuchatapplication.reporitories.GroupRepo
import com.example.tuchatapplication.reporitories.MembersRepository
import kotlinx.coroutines.launch

class ChattingViewModel(application: Application): AndroidViewModel(application) {
    private var chattingRepository: ChattingRepository? = null
    private var createResponse: Long? = null
    private var memberResponse: Long? = null
    private var chatResponse: Long? = null
    private var groups: MutableLiveData<List<Group>> = MutableLiveData()
    private var grp: MutableLiveData<Group> = MutableLiveData()
    private var grpMembers: MutableLiveData<List<Member>> = MutableLiveData()
    private var memberGroups: MutableLiveData<List<GroupDisplay>> = MutableLiveData()
    private var groupChats: MutableLiveData<List<Chat>> = MutableLiveData()

    private var groupRepo: GroupRepo? = null
    private var membersRepo: MembersRepository? = null

    init {
        chattingRepository = ChattingRepository.setInstance(application)
        groupRepo = GroupRepo.setInstance(application)
        membersRepo = MembersRepository.setInstance(application)
    }

    //create group
    fun createGroup(group: Group): Long{
        viewModelScope.launch {
            createResponse = groupRepo!!.createGroup(group)
        }

        return createResponse!!
    }

    //generate groups
    fun getGroups(): MutableLiveData<List<Group>>{
        viewModelScope.launch {
            groups.value = groupRepo!!.generateGroups()
        }

        return groups!!
    }

    //group details
    fun getGroupDetails(groupId: String): MutableLiveData<Group>{
        viewModelScope.launch {
            grp.value = groupRepo!!.getGroupDetails(groupId)
        }

        return grp
    }

    //add member
    fun addMember(member: Member): Long{
        viewModelScope.launch {
            memberResponse = membersRepo!!.addMember(member)
        }

        return memberResponse!!
    }

    //group members
    fun groupMembers(groupId: String): MutableLiveData<List<Member>>{
        viewModelScope.launch {
            grpMembers.value = membersRepo!!.getMembersByGroup(groupId)
        }

        return grpMembers
    }

    //member groups
    fun getMemberGroups(userId: String): MutableLiveData<List<GroupDisplay>>{
        var lst: ArrayList<Group>? = null
        var str: ArrayList<String>? = null
        var res: ArrayList<Group> = ArrayList()
        var chatsFinal: ArrayList<GroupDisplay>? = ArrayList()

        viewModelScope.launch {
            str = membersRepo!!.getMemberGroups(userId)
            lst = groupRepo!!.generateGroups()

            if (res.size > 0){
                for (k in 0 until res.size){
                    var chats = chattingRepository!!.getChats(res[k].group_id!!)
                    var groupDisplay: GroupDisplay = GroupDisplay()
                    groupDisplay.group_id = res[k].group_id
                    groupDisplay.group_name = res[k].group_name
                    groupDisplay.group_image = res[k].group_image
                    groupDisplay.total = chats!!.size.toString()
                    groupDisplay.message = chats!![chats!!.size - 1].message
                    groupDisplay.date = chats!![chats!!.size - 1].date
                    groupDisplay.time = chats[chats.size - 1].time

                    chatsFinal!!.add(groupDisplay)
                }
            }
        }

        if (str!!.size > 0){
            for (i in 0 until str!!.size){
                for (j in 0 until lst!!.size){
                    if (str!![i] == lst!![j].group_id){
                        res.add(lst!![j])
                    }
                }
            }

            memberGroups.value = chatsFinal
        }

        return memberGroups
    }

    //add chat
    fun addChat(chat: Chat): Long{
        viewModelScope.launch {
            chatResponse = chattingRepository!!.addChat(chat)
        }

        return chatResponse!!
    }

    //get group chats
    fun getGroupChats(groupId: String): MutableLiveData<List<Chat>>{
        viewModelScope.launch {
            groupChats.value = chattingRepository!!.getChats(groupId)
        }

        return groupChats
    }

}