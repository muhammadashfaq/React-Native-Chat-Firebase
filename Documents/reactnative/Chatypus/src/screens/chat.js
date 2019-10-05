import React, {Component} from 'react';
import {StatusBar, View, Text} from 'react-native';
import {GiftedChat} from 'react-native-gifted-chat';
import styles from '../utils/styles';
import firebaseService from '../service/firebase-service';

class Chat extends Component {
  static navigationOptions = ({navigation}) => ({
    title: navigation.state.params.roomName,
    headerTintColor: 'white',
    headerStyle: styles.messagesHeader,
    headerTitleStyle: styles.messagesTitle,
    headerBackTitleStyle: styles.messagesBackTitle,
  });

  constructor(props) {
    super(props);
    var FirebaseDB = firebaseService.database();
    var roomKey = this.props.navigation.state.params.roomKey;
    this.messagesRef = FirebaseDB.ref(`messages/${roomKey}`);
    this.state = {
      user: '',
      messages: [],
    };
  }

  componentDidMount() {
    this.setState({user: firebaseService.auth().currentUser});
    this.listenForMessages(this.messagesRef);
  }

  listenForMessages = messagesRef => {
    messagesRef.on('value', snapshot => {
      let messagesFB = [];
      snapshot.forEach(child => {
        messagesFB = [
          {
            _id: child.key,
            text: child.val().text,
            createdAt: child.val().createdAt,
            user: {
              _id: child.val().user._id,
              name: child.val().user.name,
            },
          },
          ...messagesFB,
        ];
      });
      this.setState({messages: messagesFB});
    });
  };

  addMessage = (message = {}) => {
    let messages = message[0];
    this.messagesRef.push({
      text: messages.text,
      createdAt: Date.now(),
      user: {
        _id: messages.user._id,
        name: messages.user.name,
      },
    });
  };

  render() {
    return (
      <View style={{flex: 1}}>
        <StatusBar barStyle="light-content" />
        <GiftedChat
          messages={this.state.messages}
          showUserAvatar
          onSend={this.addMessage}
          user={{
            _id: this.state.user.uid,
            name: this.state.user.email,
          }}
        />
      </View>
    );
  }
}

export default Chat;
