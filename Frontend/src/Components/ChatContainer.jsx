import React, {useEffect} from 'react'
import {useChatStore} from "../store/useChatStore.js";
import ChatHeader from "./ChatHeader.jsx";
import MessageInput from "./MessageInput.jsx";
import MessageSkeleton from "./skeletons/MessageSkeleton.jsx";
import {formatMessageTime} from "../lib/utils.js"
import { useAuthStore } from '../store/UseAuthStore'

const ChatContainer = () => {
  const {messages,getMessages,isMessagesLoading,selectedUser} = useChatStore()
  const {authUser,getProfile} = useAuthStore()

    useEffect(() => {
      getMessages(selectedUser.userId);
      getProfile()
      // console.log("Selected User is: ",selectedUser)
      // console.log("Messages fetched from ",selectedUser.name)
      // console.log("selected user are: ",selectedUser)
      // console.log("Messagesfrom ",selectedUser.name," are: ",messages)
    }, [selectedUser.userId],getMessages)

  if(isMessagesLoading) return (
      <div className='flex-1 flex flex-col overflow-auto'>
        <ChatHeader/>
        <MessageSkeleton/>
        <MessageInput/>
      </div>
  )

  return (
    <div className='flex-1 flex flex-col overflow-auto'>
      <ChatHeader />
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.chatId}
            className={`chat ${message.senderId === authUser.userId ? "chat-end" : "chat-start"}`}
            // ref={messageEndRef}
          >
            <div className=" chat-image avatar">
              <div className="size-10 rounded-full border">
                <img
                  src={
                    message.senderId === authUser.userId
                      ? authUser.image || "/avatar.png"
                      : selectedUser.image || "/avatar.png"
                  }
                  alt="profile pic"
                />
              </div>
            </div>
            <div className="chat-header mb-1">
              <time className="text-xs opacity-50 ml-1">
                {formatMessageTime(message.createdAt)}
              </time>
            </div>
            <div className="chat-bubble flex flex-col">
              {message.image && (
                <img
                  src={message.image}
                  alt="Attachment"
                  className="sm:max-w-[200px] rounded-md mb-2"
                />
              )}
              {message.message && <p>{message.message}</p>}
            </div>
          </div>
        ))}
      </div>

      <MessageInput />
    </div>
  )
}

export default ChatContainer