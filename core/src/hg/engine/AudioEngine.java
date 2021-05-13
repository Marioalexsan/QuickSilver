package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.utils.AudioUtils;
import hg.utils.MathUtils;

import java.util.Random;

// Note: GDX has a "fire and forget" type of attitude towards played Sounds
// There's little point in trying to control sound instance behavior

public class AudioEngine {
    /** A structure for describing how a Sound should play */
    public static class SoundDescription {
        public float pitch = 1f;
        public float randomPitch = 0f;
        public float minDistance = 0f; // Sound closer than this will play at 1.0 "distance" volume
        public float maxDistance = 0f; // Sound further than this will play at 0 "distance" volume

        public SoundDescription(float volume, float pitch, float randomPitch, float minDistance, float maxDistance) {
            this.pitch = pitch;
            this.randomPitch = randomPitch;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
        }
    }

    /** A structure describing a music's current state. */
    public static class MusicState {
        public String path = "";
        public float volume = 100;
        public boolean isStopping = false;
        public float transitionMod = 0f;
        public MusicState(String path, float volume) {
            this.path = path;
            this.volume = volume;
        }
    }

    private static final SoundDescription DefaultProfile = new SoundDescription(1.0f, 1.0f, 0.1f, 275f, 3200f);
    private static final int TRANSITION_TIME_IN_FRAMES = 60;

    private final Vector2 audioListener = new Vector2(0f, 0f);

    private float globalSoundVolume = 1f;
    private float globalMusicVolume = 1f;

    private Music music;
    private MusicState musicState;
    private MusicState transitionState;

    public void setGlobalSoundVolume(float volume) { this.globalSoundVolume = volume; }
    public void setGlobalMusicVolume(float volume) { this.globalMusicVolume = volume; }
    public void setListenerPosition(Vector2 audioListener) {
        this.audioListener.set(audioListener);
    }

    /** Plays a global sound, with specified volume */
    public void playSound(Sound sound, float volume) { playSound(sound, volume, null, null); }

    /** Plays a mono sound as if coming from the specified position (relative to Listener), using the default AudioDescription. */
    public void playSound(Sound sound, float volume, Vector2 position) {
        playSound(sound, volume, position, DefaultProfile);
    }

    /** Plays a mono sound as if coming from the specified position (relative to Listener), using the specified AudioDescription. */
    public void playSound(Sound sound, float volume, Vector2 position, SoundDescription data) {
        if (sound == null) return;
        if (position != null && data != null) {
            // Simulate positional sound
            float[] simData = AudioUtils.SimulatePositionalAudio(audioListener, position, data.minDistance, data.maxDistance);
            float playVolume = volume * simData[0] * globalSoundVolume;
            float playPitch = (float) (0.5 - new Random().nextDouble()) * data.randomPitch + data.pitch;
            sound.play(playVolume, playPitch, simData[1]);
        }
        else sound.play(volume * globalSoundVolume, data == null ? 1f : data.pitch, 0f);
    }

    /** Plays the provided music, with looping on, using the loop interval specified.
     * If a music is currently playing, a simple sequential fade out / fade in transition will be applied. */
    public void playMusic(String filePath, float volume) {
        if (transitionState != null) {
            transitionState = new MusicState(filePath, volume); // Swap the transition music for this one
        }
        else {
            if (musicState != null) {
                // Set up for transition. update() will take care of the rest
                transitionState = new MusicState(filePath, volume);
                musicState.isStopping = true;
            }
            else {
                // Try to set up for playing
                Music targetMusic;
                try {
                    targetMusic = Gdx.audio.newMusic(new FileHandle(filePath));
                } catch (Exception e) {
                    return; // Couldn't load it for whatever reason!
                }

                // Start the music, muted. update() will take care of the rest
                musicState = new MusicState(filePath, volume);
                music = targetMusic;
                music.setLooping(true);
                music.setVolume(0f);
                music.play();
            }
        }
    }

    /** Updates music properties and runs audio related tasks.
     * This must be called for AudioEngine to work properly. */
    public void update() {
        if (musicState == null) return;

        // Check music stop process
        if (musicState.isStopping) {
            // Apply fade out
            musicState.transitionMod -= 0.5f / TRANSITION_TIME_IN_FRAMES;
            musicState.transitionMod = (float) MathUtils.ClampValue(musicState.transitionMod, 0, 1);

            if (musicState.transitionMod <= 0.05f) {
                // Stop music, dispose of resources
                music.stop();
                music.dispose();

                // Try to process transition, if any
                musicState = transitionState;
                transitionState = null;
                if (musicState != null) {
                    // Try to set up for playing
                    Music targetMusic;
                    try {
                        targetMusic = Gdx.audio.newMusic(new FileHandle(musicState.path));
                    } catch (Exception e) {
                        musicState = null; // Couldn't load it for whatever reason!
                        return;
                    }

                    // Start the music, muted. update() will take care of the rest
                    music = targetMusic;
                    music.setLooping(true);
                    music.setVolume(0f);
                    music.play();
                }
            }
        }
        else {
            // Apply fade in
            musicState.transitionMod += 0.5f / TRANSITION_TIME_IN_FRAMES;
            musicState.transitionMod = (float) MathUtils.ClampValue(musicState.transitionMod, 0, 1);
        }

        if (musicState != null && music != null) music.setVolume(musicState.volume * globalMusicVolume * musicState.transitionMod); // Updates volume
    }

    public void stopMusic() {
        if (music != null) {
            transitionState = null;
            musicState.isStopping = true;
        }
    }

    public void cleanup() {
        // More to be implemented if needed...
        if (music != null) {
            music.stop();
            music.dispose();
        }
        musicState = null;
        transitionState = null;
    }
}
