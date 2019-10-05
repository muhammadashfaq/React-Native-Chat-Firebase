import React, {Component, Children} from 'react';
import {
  Text,
  TextInput,
  TouchableHighlight,
  StatusBar,
  ListView,
  FlatList,
  View,
  SafeAreaView,
} from 'react-native';
import styles from '../utils/styles';
import firebaseService from '../service/firebase-service';
import Entypo from 'react-native-vector-icons/Entypo';

class Home extends Component {
  static navigationOptions = {
    header: null,
  };

  constructor(props) {
    super(props);
    let firebaseDB = firebaseService.database();
    this.roomsRef = firebaseDB.ref('rooms');
    this.state = {
      rooms: [],
      newRoom: '',
    };
  }

  componentDidMount() {
    this.listenForRooms(this.roomsRef);
  }

  listenForRooms = roomsRef => {
    roomsRef.on('value', snapshot => {
      let roomsFB = [];
      snapshot.forEach(element => {
        roomsFB.push({
          name: element.val().name,
          key: element.key,
        });
      });
      this.setState({rooms: roomsFB});
    });
  };

  addRoom = async () => {
    const {newRoom} = this.state;
    if (newRoom === '') {
      return;
    }
    try {
      await this.roomsRef.push({name: newRoom});
      this.setState({newRoom: ''});
    } catch (err) {
      alert(err);
    }
  };

  openMessages(room) {
    this.props.navigation.navigate('Chat', {
      roomKey: room.key,
      roomName: room.name,
    });
  }

  renderRow = item => {
    return (
      <TouchableHighlight
        style={styles.roomLi}
        underlayColor="#fff"
        onPress={() => this.openMessages(item)}>
        <Text style={styles.roomLiText}>{item.name}</Text>
      </TouchableHighlight>
    );
  };

  render() {
    return (
      <SafeAreaView style={styles.roomsContainer}>
        <StatusBar barStyle="light-content" />
        <Text style={styles.roomsHeader}>Chatypus</Text>
        <Entypo
          onPress={() => firebaseService.auth().signOut().then(() => {
            this.props.navigation.navigate('Login')
          })}
          name="log-out"
          size={25}
          style={{alignSelf: 'flex-end', marginRight: 5, bottom: 5}}
          color={'white'}
        />
        <View style={styles.roomsInputContainer}>
          <TextInput
            style={styles.roomsInput}
            placeholder={'New Room Name'}
            onChangeText={text => this.setState({newRoom: text})}
            value={this.state.newRoom}
          />
          <TouchableHighlight
            style={styles.roomsNewButton}
            underlayColor="#fff"
            onPress={() => this.addRoom()}>
            <Text style={styles.roomsNewButtonText}>Create</Text>
          </TouchableHighlight>
        </View>
        <View style={styles.roomsListContainer}>
          <FlatList
            data={this.state.rooms}
            extraData={this.state}
            renderItem={({item}) => this.renderRow(item)}
          />
        </View>
      </SafeAreaView>
    );
  }
}

export default Home;
