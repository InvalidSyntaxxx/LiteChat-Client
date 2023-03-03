<template>
  <div class="tool">
    <div class="tool-avatar">
      <div class="tool-avatar-img" @click="showUserInfo('showUserModal')">
        <img v-if="user" :src="user.avatar" alt="" />
      </div>
      <div class="tool-avatar-name">{{ user.username }}</div>
    </div>
    <a-tooltip placement="topLeft" arrow-point-at-center>
      <div slot="title">
        <div>请文明(肆意)聊天</div>
        <div>截图粘贴可发送图片</div>
      </div>
      <a-icon type="bulb" class="tool-tip icon" />
    </a-tooltip>
    <a-icon type="skin" class="tool-skin icon" @click="showBackgroundModal = true" />
    <!-- <a href="https://github.com/InvalidSyntaxxx/LiteChat-Client" target="_blank" class="tool-github icon"><a-icon type="github"/></a> -->
    <a-icon type="appstore" class="tool-github icon" @click="showAppModal = true" />

    <a-modal title="应用服务" :visible="showAppModal" footer="" @cancel="showAppModal = false">
      <div>
        <div class="bookDownload">
          <a-icon type="book" class="icon" /> 书籍下载
          <a href="https://1lib.cf/">1lib.cf</a>
        </div>
        <a-input-search placeholder="请输入书名" style="width: 90%; margin: 2px 0 0 20px;" @search="onSearchBook"></a-input-search>
        <div class="game">
          <a-icon type="build" class="icon" /> 游戏
          <div style="padding: 3px 0;">
            <a href="https://wangwangyz.site/my/games/%E4%B8%80%E4%B8%AA%E9%83%BD%E4%B8%8D%E8%83%BD%E6%AD%BB/">
              一个都不能死
            </a>
          </div>
          <div>
            <a href="https://wangwangyz.site/my/games/%E9%AD%94%E6%96%B9">3D魔方</a>
          </div>
        </div>
      </div>
    </a-modal>
    <a-icon class="tool-out icon" type="logout" @click="logout" />
    <a-modal title="用户信息" :visible="showUserModal" footer="" @cancel="showUserModal = false">
      <div class="tool-user">
        <div @mouseover="showUpload = true" @mouseleave="showUpload = false" class="tool-user-avatar"
          :class="{ active: showUpload || uploading }">
          <a-avatar :src="user.avatar" class="img" :size="120"></a-avatar>
          <a-upload v-if="showUpload && !isTourist && !uploading" class="tool-user-upload" :show-upload-list="false"
            :before-upload="beforeUpload">
            <div class="text">
              <a-icon type="upload" style="margin-right: 4px;" />
              <span>更换头像</span>
            </div>
          </a-upload>
          <a-icon class="loading" v-if="uploading" type="loading" spin />
        </div>
        <div class="tool-user-info">
          <div class="tool-user-title">更改用户名</div>
          <a-input class="tool-user-input" v-model="username" placeholder="请输入用户名"></a-input>
          <a-button type="primary" @click="changeUserName">确认</a-button>
        </div>
        <div class="tool-user-info">
          <div class="tool-user-title">更改密码</div>
          <a-input-password class="tool-user-input" v-model="password" placeholder="请输入密码"></a-input-password>
          <a-button type="primary" @click="changePassword">确认</a-button>
        </div>
      </div>
    </a-modal>
    <a-modal title="主题" :visible="showBackgroundModal" footer="" @cancel="showBackgroundModal = false">
      <div class="tool-user-info">
        <div class="tool-user-title" style="width: 65px;">
          <span>背景图</span>
          <a-tooltip placement="topLeft" arrow-point-at-center>
            <div slot="title">
              <span>输入空格时为默认背景, 支持 jpg, png, gif等格式</span>
            </div>
            <a-icon type="exclamation-circle" style="margin-left: 5px;" />
          </a-tooltip>
        </div>
        <a-input v-model="background" class="tool-user-input" placeholder="请输入背景图片网址"></a-input>
        <a-button type="primary" @click="changeBackground">确认</a-button>
      </div>
      <div class="tool-recommend">
        <div class="recommend" @click="
          setBackground(
            'https://images.weserv.nl/?url=https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/23fa890c0c244db1b2d6e0927113475c~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85'
          )
        ">
          <img
            src="https://images.weserv.nl/?url=https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/23fa890c0c244db1b2d6e0927113475c~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85"
            alt="" />
          <span class="text">阿童木</span>
        </div>
        <div class="recommend" @click="
          setBackground('https://images.weserv.nl/?url=https://raw.githubusercontent.com/alexanderbast/vscode-snazzy/master/sample.jpg')
        ">
          <img
            src="https://images.weserv.nl/?url=https://raw.githubusercontent.com/alexanderbast/vscode-snazzy/master/sample.jpg"
            alt="" />
          <span class="text">VSCode摸鱼</span>
        </div>
        <div class="recommend" @click="
          setBackground(
            'https://images.weserv.nl/?url=https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/453b8ebcdefa46a69c620da13f346ce2~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85'
          )
        ">
          <img
            src="https://images.weserv.nl/?url=https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/453b8ebcdefa46a69c620da13f346ce2~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85"
            alt="" />
          <span class="text">山谷</span>
        </div>
        <div class="recommend"
          @click="setBackground('https://pic2.zhimg.com/v2-f76706d67343c66b08c937ec6bc42942_r.jpg?source=1940ef5c')">
          <img src="https://pic2.zhimg.com/v2-f76706d67343c66b08c937ec6bc42942_r.jpg?source=1940ef5c" alt="" />
          <span class="text">云朵</span>
        </div>
        <div class="recommend" @click="
          setBackground(
            'https://images.weserv.nl/?url=https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cc98cbc4ca284fc0aa509b12db0e325e~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85'
          )
        ">
          <img
            src="https://images.weserv.nl/?url=https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cc98cbc4ca284fc0aa509b12db0e325e~tplv-k3u1fbpfcp-zoom-1.image?imageView2/2/w/800/q/85"
            alt="" />
          <span class="text">少女</span>
        </div>
        <div class="recommend" @click="setBackground('https://picb.zhimg.com/v2-263525f6c912d300abfa0eed3acbfd4b_r.jpg')">
          <img src="https://picb.zhimg.com/v2-263525f6c912d300abfa0eed3acbfd4b_r.jpg" alt="" />
          <span class="text">猫咪</span>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { setUserAvatar } from '@/api/apis';
import { DEFAULT_BACKGROUND, DEFAULT_GROUP } from '@/const/index';
import { namespace } from 'vuex-class';
import * as apis from '@/api/apis';
import { processReturn, nameVerify, passwordVerify } from '@/utils/common';
const appModule = namespace('app');
const chatModule = namespace('chat');

@Component
export default class GenalTool extends Vue {
  @appModule.Getter('user') user: User;
  @appModule.Mutation('set_background') setBackground: Function;
  @appModule.Mutation('set_user') setUser: Function;
  @chatModule.Getter('socket') socket: SocketIOClient.Socket;
  @chatModule.Mutation('set_user_gather') setUserGather: Function;
  showUpload: boolean = false;
  showUserModal: boolean = false;
  showBackgroundModal: boolean = false;
  showAppModal: boolean = false;
  username: string = '';
  password: string = '';
  background: string = '';
  uploading: boolean = false;
  avatar: any = '';

  style: string;

  @Watch('user')
  userChange() {
    this.username = this.user.username;
    this.password = this.user.password;
  }

  created() {
    this.username = this.user.username;
    this.password = this.user.password;
  }

  logout() {
    this.$emit('logout');
  }
  onSearchBook(value: string) {
    window.location.href = 'https://1lib.cf/s/' + value + '?';
  }
  isTourist() {
    return this.user.username === '游客';
  }
  showUserInfo() {
    this.username = this.user.username;
    this.showUserModal = true;
  }
  async changeUserName() {
    if (!nameVerify(this.username)) {
      return;
    }
    if (this.isTourist()) {
      this.$message.error('游客无权限！请您注册账号体验所有功能');
      return;
    }
    let user: User = JSON.parse(JSON.stringify(this.user));
    user.username = this.username;
    let res = await apis.patchUserName(user);
    let data = processReturn(res);
    if (data) {
      console.log(data);
      this.setUser(data);
      this.setUserGather(data);
      // 通知其他用户个人信息改变
      this.socket.emit('joinGroupSocket', {
        groupId: DEFAULT_GROUP,
        userId: data.userId,
      });
    }
  }

  async changePassword() {
    if (!passwordVerify(this.password)) {
      return;
    }
    if (this.isTourist()) {
      this.$message.error('游客无权限！请您注册账号体验所有功能');
      return;
    }
    let user: User = JSON.parse(JSON.stringify(this.user));
    let res = await apis.patchPassword(user, this.password);
    let data = processReturn(res);
    if (data) {
      this.setUser(data);
      this.setUserGather(data);
    }
  }

  beforeUpload(file: any) {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg' || file.type === 'image/gif';
    if (!isJpgOrPng) {
      return this.$message.error('请上传jpeg/jpg/png/gif格式的图片!');
    }
    const isLt2M = file.size / 1024 / 1024 < 5;
    if (!isLt2M) {
      return this.$message.error('图片必须小于5M!');
    }
    this.avatar = file;
    this.handleUpload();
    return false;
  }

  async handleUpload() {
    this.uploading = true;
    const formData = new FormData();
    formData.append('avatar', this.avatar);
    formData.append('userId', this.user.userId);
    formData.append('password', this.user.password);
    let data = processReturn(await setUserAvatar(formData));
    if (data) {
      this.setUser(data);
      this.setUserGather(data);
      this.uploading = false;
      this.showUpload = false;
      // 通知其他用户个人信息改变
      this.socket.emit('joinGroupSocket', {
        groupId: DEFAULT_GROUP,
        userId: data.userId,
      });
    }
  }

  changeBackground() {
    if (!this.background.trim().length) {
      this.setBackground(DEFAULT_BACKGROUND);
    } else {
      this.setBackground(this.background);
    }
    this.showBackgroundModal = false;
  }
}
</script>
<style lang="scss" scoped>
.tool {
  padding: 10px 5px;
  height: 98%;
  position: relative;

  .tool-avatar {
    margin-top: 3px;

    .tool-avatar-img {
      margin: 0 auto;
      width: 55px;
      height: 55px;
      border-radius: 50%;
      overflow: hidden;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .tool-avatar-name {
      color: #fff;
      overflow: hidden; //超出的文本隐藏
      text-overflow: ellipsis; //溢出用省略号显示
      white-space: nowrap; //溢出不换行
      margin-top: 2px;
    }
  }

  .tool-tip {
    bottom: 190px;
  }

  .tool-skin {
    bottom: 130px;
  }

  .tool-github {
    color: rgba(255, 255, 255, 0.85);
    bottom: 70px;
  }

  .tool-out {
    bottom: 10px;
  }

  .icon {
    display: flex;
    flex-direction: column;
    position: absolute;
    left: 25px;
    font-size: 25px;
    cursor: pointer;
    z-index: 100;

    &:hover {
      color: skyblue;
    }
  }
}

.tool-user {
  text-align: center;
  font-size: 16px;

  .tool-user-avatar {
    position: relative;
    width: 120px;
    overflow: hidden;
    margin: 0 auto 24px;
    border-radius: 50%;
    cursor: pointer;

    .tool-user-upload {
      .text {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        line-height: 120px;
        font-weight: bold;
      }
    }

    .loading {
      position: absolute;
      top: 50%;
      left: 50%;
      margin: -18px 0 0 -18px;
      font-size: 35px;
      font-weight: bold;
      color: #fff;
    }

    .img {
      transition: 0.2s all linear;
    }

    &.active {
      .img {
        filter: blur(3px);
      }
    }
  }
}

.tool-user-info {
  display: flex;
  justify-content: left;
  align-items: center;

  .tool-user-input {
    flex: 1;
    margin-right: 5px;
  }

  .tool-user-title {
    display: flex;
    align-items: center;
    width: 90px;
    text-align: left;
    font-weight: bold;
    word-break: keep-all;
    margin-right: 15px;
  }

  &:nth-child(2) {
    margin-bottom: 15px;
  }
}

.tool-recommend {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;

  .recommend {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    width: 100px;
    height: 100px;
    margin: 15px 10px 0;
    overflow: hidden;
    cursor: pointer;
    transition: 0.3s all linear;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    span {
      position: absolute;
      color: rgba(255, 255, 255, 0.85);
      font-weight: 600;
      transition: 0.3s all linear;
      opacity: 0;
    }

    &:hover {
      box-shadow: 1px 5px 10px gray;

      span {
        opacity: 1;
      }
    }
  }
}

@media screen and (max-width: 788px) {
  .tool-recommend {
    font-size: 12px;

    .recommend {
      width: 80px;
      height: 80px;
    }
  }
}

.bookDownload,
.game {
  font-size: 18px;
}

.game {
  padding: 20px 0 0 0;
}
</style>
