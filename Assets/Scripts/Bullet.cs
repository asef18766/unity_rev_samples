using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Bullet : MonoBehaviour
{
        [SerializeField] private List<Sprite> motions;
        [SerializeField] private float frameTime = 0.2f;
        private int _motionIdx = 0;
        private SpriteRenderer _spriteRenderer;
        private void Start()
        {
                _spriteRenderer = GetComponent<SpriteRenderer>();
                StartCoroutine(PlayAni());
                StartCoroutine(SelfDestroy());
        }

        private IEnumerator PlayAni()
        {
                while (true)
                {
                        _spriteRenderer.sprite = motions[_motionIdx];
                        _motionIdx = (_motionIdx + 1) % motions.Count;
                        yield return new WaitForSeconds(frameTime);
                }
        }

        private IEnumerator SelfDestroy()
        {
                yield return new WaitForSeconds(5);
                Destroy(gameObject);
        }
}