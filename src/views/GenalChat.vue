<template>
  <div class="chat" :style="{
    '--bg-image': `url('${background}')`,
  }">
    <genal-music></genal-music>
    <div class="chat-part1" v-if="visibleTool">
      <genal-tool @logout="logout"></genal-tool>
    </div>
    <div class="chat-part2">
      <genal-search @addGroup="addGroup" @joinGroup="joinGroup" @addFriend="addFriend" @setActiveRoom="setActiveRoom">
      </genal-search>
      <genal-room @setActiveRoom="setActiveRoom"></genal-room>
    </div>
    <div class="chat-part3">
      <div class="chat-tool">
        <a-icon type="menu-fold" @click="toggleTool" v-if="visibleTool" />
        <a-icon type="menu-unfold" @click="toggleTool" v-else />
        <a-badge class="chat-tool-badge" :count="unreadMessage">
          <a-icon :style="{ fontSize: '26px', padding: '0 0 0 18px' }" type="message" @click="toggleDrawer" />
        </a-badge>
      </div>
      <genal-message v-if="activeRoom"></genal-message>
    </div>
    <a-drawer placement="left" :closable="false" :visible="visibleDrawer" @close="toggleDrawer" style="height:100%">
      <div class="chat-drawer">
        <genal-search @addGroup="addGroup" @joinGroup="joinGroup" @addFriend="addFriend" @setActiveRoom="setActiveRoom">
        </genal-search>
        <genal-room @setActiveRoom="setActiveRoom"></genal-room>
      </div>
    </a-drawer>
    <genal-join @register="handleRegister" @login="handleLogin" :showModal="showModal"></genal-join>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import GenalTool from '@/components/GenalTool.vue';
import GenalJoin from '@/components/GenalJoin.vue';
import GenalRoom from '@/components/GenalRoom.vue';
import GenalMessage from '@/components/GenalMessage.vue';
import GenalSearch from '@/components/GenalSearch.vue';
import GenalMusic from '@/components/GenalMusic.vue';
import { namespace } from 'vuex-class';
import chat from '@/store/modules/chat';
import cookie from 'js-cookie';
import { SET_GROUP_GATHER } from '@/store/modules/chat/mutation-types';
const appModule = namespace('app');
const chatModule = namespace('chat');

@Component({
  components: {
    GenalTool,
    GenalJoin,
    GenalRoom,
    GenalMessage,
    GenalSearch,
    GenalMusic,
  },
})
export default class GenalChat extends Vue {
  @appModule.Getter('user') user: User;
  @appModule.Mutation('clear_user') clearUser: Function;
  @appModule.Action('login') login: Function;
  @appModule.Action('register') register: Function;
  @appModule.Getter('background') background: string;

  @chatModule.Getter('socket') socket: SocketIOClient.Socket;
  @chatModule.Getter('userGather') userGather: FriendGather;
  @chatModule.Getter('groupGather') groupGather: GroupGather;
  @chatModule.Getter('friendGather') friendGather: FriendGather;
  @chatModule.Getter('activeRoom') activeRoom: Friend & Group;
  @chatModule.Mutation('set_active_room') _setActiveRoom: Function;
  @chatModule.Mutation('set_group_gather') _setGroupGather: Function; // 设置初始状态
  @chatModule.Mutation('set_friend_gather') _setFriendGather: Function;
  @chatModule.Action('connectSocket') connectSocket: Function;
  @chatModule.Getter('unReadGather') unReadGather: UnReadGather;

  chatArr: Array<Group | Friend> = [];
  unreadMessage: number = 0;
  showModal: boolean = false;
  visibleDrawer: boolean = false;
  visibleTool: boolean = true;

  created() {
    if (!this.user.userId) {
      this.showModal = true;
      // this.setGather();
    } else {
      this.handleJoin();
    }
  }
  @Watch('groupGather', { deep: true })
  changeGroupGather() {
    this.changeUnreadMessage();
  }
  @Watch('friendGather', { deep: true })
  changeFriendGather() {
    this.changeUnreadMessage();
  }

  @Watch('unReadGather', { deep: true })
  UnreadMessage() {
    this.changeUnreadMessage();
  }

  //  1. 判断是否在当前窗口，从消息窗口转过来时、从聊天窗口转过来时清空标点；
  //  2. 独立设置两个状态，有新消息时 unreadMessage 为

  changeUnreadMessage() {
    this.chatArr = [];
    let groups = Object.values(this.groupGather);
    let friends = Object.values(this.friendGather);
    this.chatArr = [...groups, ...friends];
    this.unreadMessage = 0;
    // console.log("initial:" + this.unreadMessage);
    for (let chat in this.chatArr) {
      if ((this.chatArr[chat] as Group).groupId) {
        let groupId = (this.chatArr[chat] as Group).groupId;
        this.unreadMessage += this.unReadGather[(this.chatArr[chat] as Group).groupId] || 0;
        // console.log('聊天室id：' + this.unReadGather[(this.chatArr[chat] as any).groupId] + '未读消息：' + this.unreadMessage);
        // this.unreadMessage = Number(cookie.get('unReadCount')) || 0;
      } else {
        this.unreadMessage += this.unReadGather[(this.chatArr[chat] as Friend).userId] || 0;
        // console.log('用户id：' + this.unReadGather[(this.chatArr[chat] as Friend).userId] + '未读消息：' + this.unreadMessage);
      }
    }
  }
  // 登录
  async handleLogin(user: User) {
    let res = await this.login(user);
    if (res) {
      // 进入系统事件
      this.handleJoin();
    }
  }

  // 注册
  async handleRegister(user: User) {
    let res = await this.register(user);
    if (res) {
      // 进入系统事件
      this.handleJoin();
    }
  }

  // 进入系统初始化事件
  async handleJoin() {
    this.showModal = false;
    this.connectSocket();
  }

  // 创建群组
  addGroup(groupName: string) {
    this.socket.emit('addGroup', {
      userId: this.user.userId,
      groupName: groupName,
      createTime: new Date().valueOf(),
    });
  }

  // 加入群组
  joinGroup(groupId: string) {
    this.socket.emit('joinGroup', {
      userId: this.user.userId,
      groupId: groupId,
    });
  }

  // 添加好友
  addFriend(friendId: string) {
    console.log(this.user);
    this.socket.emit('addFriend', {
      userId: this.user.userId,
      friendId: friendId,
      createTime: new Date().valueOf(),
    });
  }

  // 设置当前聊天窗
  setActiveRoom(room: Friend & Group) {
    this._setActiveRoom(room);
  }

  // 注销
  logout() {
    this.clearUser();
    this.$router.go(0);
  }

  toggleDrawer() {
    this.visibleDrawer = !this.visibleDrawer;
  }

  toggleTool() {
    this.visibleTool = !this.visibleTool;
  }
}
</script>
<style lang="scss" scoped>
.chat {
  font-size: 16px;
  z-index: 999;
  max-width: 1000px;
  min-width: 300px;
  width: 100%;
  height: 80%;
  max-height: 900px;
  min-height: 470px;
  position: relative;
  margin: auto 20px;
  box-shadow: 10px 20px 80px rgba(0, 0, 0, 0.8);
  display: flex;
  border-radius: 5px;
  overflow: hidden;

  .chat-part1 {
    width: 74px;
    height: 100%;
    background-color: rgb(0, 0, 0, 0.7);
  }

  .chat-part2 {
    width: 230px;
    height: 100%;
    background-color: rgb(0, 0, 0, 0.3);
  }

  .chat-part3 {
    flex: 1;
    height: 100%;
    background-color: rgb(0, 0, 0, 0.2);
    overflow-y: hidden;
    position: relative;

    .chat-group {
      height: 53px;
      border-bottom: 1px solid #ccc;
      line-height: 50px;
      font-weight: bold;
    }
  }

  .chat-team {
    display: none;
  }

  .chat-tool {
    display: none;
  }

  // .chat-list {
  // display: none;
  // }
}

.chat::after {
  content: '';
  background: var(--bg-image) 0 / cover fixed;
  position: absolute;
  object-fit: cover;
  width: 100%;
  height: 100%;
  filter: blur(10px);
  transform: scale(1.08);
  z-index: -1;
}

@media screen and (max-width: 768px) {
  .chat {
    margin: 0;
    height: 100%;

    .chat-part2 {
      display: none;
    }

    .chat-team {
      display: block !important;
      position: absolute;
      font-size: 25px;
      top: 17px;
      left: 60px;
      z-index: 999;

      &:active {
        color: skyblue;
      }
    }

    .chat-tool {
      display: block !important;
      position: absolute;
      font-size: 25px;
      top: 13px;
      left: 20px;
      z-index: 999;

      &:active {
        color: skyblue;
      }
    }
  }

  .chat::after {
    filter: blur(0);
  }
}
</style>
