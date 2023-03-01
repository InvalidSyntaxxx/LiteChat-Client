<template>
  <div class="message-input" v-if="activeRoom">
    <a-popover placement="topLeft" trigger="hover" class="message-popver">
      <template slot="content">
        <a-tabs default-key="1" size="small">
          <a-tab-pane key="1" tab="Emoji">
            <genal-emoji @addEmoji="addEmoji"></genal-emoji>
          </a-tab-pane>
          <a-tab-pane key="2" tab="工具">
            <div class="message-tool-item">
              <a-upload :show-upload-list="false" :before-upload="beforeImgUpload">
                <div class="message-tool-contant">
                  <a-icon type="picture" :style="{ fontSize: '42px' }" />
                  <!-- <img src="~@/assets/photo.png" class="message-tool-item-img" alt="" /> -->
                  <!-- <div class="message-tool-item-text">图片</div> -->
                </div>
              </a-upload>
            </div>
          </a-tab-pane>
        </a-tabs>
      </template>
      <div class="messagte-tool-icon"><a-icon type="plus" :style="{ color: '#00BFFF', fontSize: '1.2rem' }" /></div>
    </a-popover>
    <a-input autocomplete="off" type="text" placeholder="说点什么.." v-model="text" ref="input" autoFocus style="color:#000;"
      @pressEnter="throttle(preSendMessage)" />
    <img class="message-input-button" @click="throttle(preSendMessage)" src="~@/assets/send.png" alt="" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import GenalEmoji from './GenalEmoji.vue';
import { namespace } from 'vuex-class';
import axios from 'axios';
const chatModule = namespace('chat');
const appModule = namespace('app');

@Component({
  components: {
    GenalEmoji,
  },
})
export default class GenalInput extends Vue {
  @appModule.Getter('user') user: User;
  @appModule.Getter('mobile') mobile: boolean;

  @chatModule.State('activeRoom') activeRoom: Group & Friend;
  @chatModule.Getter('socket') socket: SocketIOClient.Socket;
  @chatModule.Getter('dropped') dropped: boolean;
  @chatModule.Getter('groupGather') groupGather: GroupGather;
  @chatModule.Getter('userGather') userGather: FriendGather;

  text: string = '';
  lastTime: number = 0;

  mounted() {
    this.initPaste();
  }

  /**
   * 点击切换房间进入此方法
   */
  @Watch('activeRoom')
  changeActiveRoom() {
    this.$nextTick(() => {
      this.focusInput();
    });
  }

  /**
   * 监听图片粘贴事件
   */
  initPaste() {
    document.addEventListener('paste', (event) => {
      let items = event.clipboardData && event.clipboardData.items;
      let url = window.URL || window.webkitURL;
      let file = null;
      if (items && items.length) {
        // 检索剪切板items
        for (let i = 0; i < items.length; i++) {
          if (items[i].type.indexOf('image') !== -1) {
            file = items[i].getAsFile();
            break;
          }
        }
      }
      if (file) {
        this.throttle(this.handleImgUpload, file);
      }
    });
  }

  /**
   * 消息发送节流
   */
  throttle(fn: Function, file?: File) {
    let nowTime = +new Date();
    if (nowTime - this.lastTime < 500) {
      return this.$message.error('消息发送太频繁！');
    }
    fn(file);
    this.lastTime = nowTime;
  }

  /**
   * 启用机器人对话，引入GPT-3
   * 1.发送消息api得到gpt回话
   * 2.机器人用户向群组回话
   */

  async handlePostGPT(info: string, type: string, Id: string): Promise<string> {
    axios
      .post(
        'https://api.openai.com/v1/completions',
        {
          prompt: info,
          max_tokens: 2048,
          model: 'text-davinci-003',
        },
        {
          headers: {
            'content-type': 'application/json',
            Authorization: 'Bearer apikey',
          },
        }
      )
      .then((response) => {
        if (type === 'group') {
          this.socket.emit('groupMessage', {
            userId: '26db8ec2-301d-4672-9de2-b6ef074b505f',
            groupId: Id,
            content: this.regexGPT(response.data.choices[0].text),
            messageType: 'text',
          });
        } else {
          console.log(response.data)
          this.socket.emit('friendMessage', {
            userId: '26db8ec2-301d-4672-9de2-b6ef074b505f',
            friendId: this.user.userId,
            content: this.regexGPT(response.data.choices[0].text),
            messageType: 'text',
          });
        }
      })
      .catch((err) => {
        console.log(err);
        if (type === 'group') {
          this.socket.emit('groupMessage', {
            userId: '26db8ec2-301d-4672-9de2-b6ef074b505f',
            groupId: Id,
            content: '机器人出了点问题，请稍后...',
            messageType: 'text',
          });
        } else {
          this.socket.emit('friendMessage', {
            userId: '26db8ec2-301d-4672-9de2-b6ef074b505f',
            friendId: this.user.userId,
            content: '机器人出了点问题，请稍后...',
            messageType: 'text',
          });
        }
        return '';
      });
    return '';
  }
  /**
   * ChatGPT回话修改
   */
  regexGPT(answer: string): string {
    answer = answer.replace(/^['?','!','。','.',']/, '');
    return answer;
  }
  /**
   * 消息发送前校验
   */
  preSendMessage() {
    if (!this.text.trim()) {
      this.$message.error('不能发送空消息!');
      return;
    }
    if (this.text.length > 320) {
      this.$message.error('消息太长!');
      return;
    }
    if (this.activeRoom.groupId) {
      this.sendMessage({ type: 'group', message: this.text, messageType: 'text' });
      if (this.text.substring(0, 4) == '@机器人') {
        this.handlePostGPT(this.text.slice(4), 'group', this.activeRoom.groupId);
      }
    } else if (this.activeRoom.userId === '26db8ec2-301d-4672-9de2-b6ef074b505f') { //通过activeRoom获取当前用户id
      this.sendMessage({ type: 'friend', message: this.text, messageType: 'text' });
      this.handlePostGPT(this.text, 'friend', this.activeRoom.userId);
    } else {
      this.sendMessage({ type: 'friend', message: this.text, messageType: 'text' });
    }
    this.text = '';
  }

  /**
   * 消息发送
   */
  sendMessage(data: SendMessage) {
    if (data.type === 'group') {
      this.socket.emit('groupMessage', {
        userId: this.user.userId,
        groupId: this.activeRoom.groupId,
        content: data.message,
        width: data.width,
        height: data.height,
        messageType: data.messageType,
      });
    } else {
      this.socket.emit('friendMessage', {
        userId: this.user.userId,
        friendId: this.activeRoom.userId,
        content: data.message,
        width: data.width,
        height: data.height,
        messageType: data.messageType,
      });
    }
  }

  /**
   * 添加emoji到input
   */
  addEmoji(emoji: string) {
    const inputDom = (this.$refs.input as Vue).$el as HTMLFormElement;
    if (inputDom.selectionStart || inputDom.selectionStart === '0') {
      // 得到光标前的位置
      const startPos = inputDom.selectionStart;
      // 得到光标后的位置
      const endPos = inputDom.selectionEnd;
      // 在加入数据之前获得滚动条的高度
      const restoreTop = inputDom.scrollTop;
      // emoji表情插入至当前光标指定位置
      this.text = this.text.substring(0, startPos) + emoji + this.text.substring(endPos, this.text.length);
      // 如果滚动条高度大于0
      if (restoreTop > 0) {
        // 返回
        inputDom.scrollTop = restoreTop;
      }
      inputDom.focus();
      // 设置光标位置至emoji表情后一位
      const position = startPos + emoji.length;
      if (inputDom.setSelectionRange) {
        inputDom.focus();
        setTimeout(() => {
          inputDom.setSelectionRange(position, position);
        }, 10);
      } else if (inputDom.createTextRange) {
        const range = inputDom.createTextRange();
        range.collapse(true);
        range.moveEnd('character', position);
        range.moveStart('character', position);
        range.select();
      }
    } else {
      this.text += emoji;
      inputDom.focus();
    }
  }

  /**
   * focus input框
   */
  focusInput() {
    if (!this.mobile) {
      // @ts-ignore
      this.$refs.input.focus();
    }
  }

  /**
   * 计算图片的比例
   */
  getImageSize(data: ImageSize) {
    let { width, height } = data;
    if (width > 335 || height > 335) {
      if (width > height) {
        height = 335 * (height / width);
        width = 335;
      } else {
        width = 335 * (width / height);
        height = 335;
      }
    }
    return {
      width,
      height,
    };
  }

  /**
   * 图片上传校验
   * @params file
   */
  beforeImgUpload(file: File) {
    this.throttle(this.handleImgUpload, file);
    return false;
  }

  /**
   * 图片消息发送
   * @params file
   */
  async handleImgUpload(imageFile: File) {
    const isJpgOrPng =
      imageFile.type === 'image/jpeg' || imageFile.type === 'image/png' || imageFile.type === 'image/jpg' || imageFile.type === 'image/gif';
    if (!isJpgOrPng) {
      return this.$message.error('请选择jpeg/jpg/png/gif格式的图片!');
    }
    const isLt1M = imageFile.size / 1024 / 1024 < 5;
    if (!isLt1M) {
      return this.$message.error('图片必须小于5M!');
    }
    let image = new Image();
    let url = window.URL || window.webkitURL;
    image.src = url.createObjectURL(imageFile);
    image.onload = () => {
      let imageSize: ImageSize = this.getImageSize({ width: image.width, height: image.height });
      this.sendMessage({
        type: this.activeRoom.groupId ? 'group' : 'friend',
        message: imageFile,
        width: imageSize.width,
        height: imageSize.height,
        messageType: 'image',
      });
    };
  }
}
</script>
<style lang="scss" scoped>
.message-input {
  display: flex;
  flex-wrap: nowrap;
  position: absolute;
  width: 100%;
  bottom: 0px;

  input {
    height: 40px;
  }

  .message-input-button {
    width: 30px;
    cursor: pointer;
    position: absolute;
    right: 10px;
    top: 4px;
  }
}

//输入框样式
.ant-input {
  padding: 0 50px 0 50px;
}

// 消息工具样式
.messagte-tool-icon {
  position: absolute;
  left: 0;
  top: 0;
  width: 50px;
  height: 40px;
  text-align: center;
  line-height: 42px;
  font-size: 16px;
  cursor: pointer;
  z-index: 99;
}

.message-tool-item {
  width: 0px;
  height: 240px;
  cursor: pointer;

  .message-tool-contant {
    width: 50px;
    padding: 5px;
    border-radius: 5px;
    transition: all linear 0.2s;

    .message-tool-item-img {
      width: 40px;
    }

    .message-tool-item-text {
      text-align: center;
      font-size: 10px;
    }

    &:hover {
      background: rgba(135, 206, 235, 0.6);
    }
  }
}
</style>
