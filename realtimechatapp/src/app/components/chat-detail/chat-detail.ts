import { Component, ElementRef, ViewChild, AfterViewChecked, signal, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-detail.html'
})
export class ChatDetail implements AfterViewChecked {
  chatId = input<string | null>(null);
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  messageText = signal('');
  messages = signal<any[]>([
    { id: '1', text: 'Hey Sarah! How is the code going?', sender: 'other', time: '10:30 AM' },
    { id: '2', text: 'Working on the Angular signals migration! 🚀', sender: 'me', time: '10:32 AM' }
  ]);

  ngAfterViewChecked() { this.scrollToBottom(); }

  private scrollToBottom(): void {
    this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
  }

  handleSend() {
    const text = this.messageText().trim();
    if (!text) return;

    this.messages.update(m => [...m, {
      id: Date.now().toString(),
      text,
      sender: 'me',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isCommand: text.startsWith('/')
    }]);
    this.messageText.set('');
  }
}